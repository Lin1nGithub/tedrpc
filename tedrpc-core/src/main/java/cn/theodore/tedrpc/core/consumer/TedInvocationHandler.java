package cn.theodore.tedrpc.core.consumer;

import cn.theodore.tedrpc.core.api.*;
import cn.theodore.tedrpc.core.consumer.http.OkHttpInvoker;
import cn.theodore.tedrpc.core.goverance.SlidingTimeWindow;
import cn.theodore.tedrpc.core.meta.InstanceMeta;
import cn.theodore.tedrpc.core.util.MethodUtils;
import cn.theodore.tedrpc.core.util.TypeUtils;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 消费端的动态代理处理类.
 *
 * @author linkuan
 */
@Slf4j
public class TedInvocationHandler implements InvocationHandler {

    private Class<?> service;

    private RpcContext context;

    /**
     * 可提供服务的providers
     */
    private final List<InstanceMeta> providers;

    /**
     * 隔离的providers
     */
    private List<InstanceMeta> isolateProviders = new ArrayList<>();

    private final List<InstanceMeta> halfOpenProvides = new ArrayList<>();

    private HttpInvoker httpInvoker;

    private Map<String, SlidingTimeWindow> windows = new HashMap<>();

    private ScheduledExecutorService executor;

    public TedInvocationHandler(Class<?> clz, RpcContext context, List<InstanceMeta> providers) {
        this.service = clz;
        this.context = context;
        this.providers = providers;
        int timeout = Integer.parseInt(context.getParameters().getOrDefault("app.timeout", "1000"));
        this.httpInvoker = new OkHttpInvoker(timeout);
        this.executor = Executors.newScheduledThreadPool(1);
        this.executor.scheduleWithFixedDelay(this::halfOpen, 10, 60, TimeUnit.SECONDS);
    }

    /**
     * 半开操作
     */
    private void halfOpen() {
        log.debug(" ====> half open isolateProviders: {}", isolateProviders);
        halfOpenProvides.clear();
        halfOpenProvides.addAll(isolateProviders);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        if (MethodUtils.checkLocalMethod(method)) {
            return null;
        }

        // 重试次数
        int retries = Integer.parseInt(context.getParameters().getOrDefault("app.retries", "1"));
        while (retries-- > 0) {

            log.info(" ====> retries:{}" , retries);

            try {
                RpcRequest rpcRequest = new RpcRequest();
                rpcRequest.setService(service.getCanonicalName());
                rpcRequest.setMethodSign(MethodUtils.methodSign(method));
                rpcRequest.setArgs(args);

                // 请求前的过滤
                for (Filter filter : this.context.getFilters()) {
                    Object preResult = filter.preFilter(rpcRequest);
                    if (null != preResult) {
                        log.info(filter.getClass().getName(),"====> preFilter:{}", preResult);
                        return preResult;
                    }
                }

                RpcResponse<?> rpcResponse;
                Object result;
                InstanceMeta instance = null;

                // 线程安全
                synchronized (halfOpenProvides) {
                    // 探活操作
                    if (halfOpenProvides.isEmpty()) {
                        List<InstanceMeta> instances = context.getRouter().route(providers);
                        instance = context.getLoadBalancer().choose(instances);
                        log.info("loadBalancer.choose(nodes) ==> {}", instance);
                    } else {
                        instance = halfOpenProvides.remove(0);
                        log.debug(" check alive instance ==> {}", instance);
                    }
                }

                String url = instance.toUrl();
                // 故障隔离
                try {
                    rpcResponse = httpInvoker.post(rpcRequest, url);
                    result = caseReturnResult(method, rpcResponse);
                } catch (Exception e) {
                    // 故障规则统计和隔离
                    // 每一次异常,记录一次, 统计30秒的异常数
                    SlidingTimeWindow window = windows.get(url);
                    if (window == null) {
                        window = new SlidingTimeWindow();
                        windows.put(url, window);
                    }

                    window.record(System.currentTimeMillis());
                    log.debug("instance {} in winodw with {}", url, window.getSum());
                    // 发生了10次,就做故障隔离
                    if (window.getSum() >= 10) {
                        isolate(instance);
                    }
                    throw e;
                }

                // 探活检查
                // 本次请求成功 && 当前实例不在providers中
                // 说明这是探活节点 并且探活成功 那么需要从隔离的集合中剔除 加回正常的节点
                synchronized (providers) {
                    if (!providers.contains(instance)) {
                        isolateProviders.remove(instance);
                        providers.add(instance);
                        log.debug("instance:{} is recovered, isolateProviders:{}, providers:{}", instance, isolateProviders, providers);
                    }
                }

                // 请求后的过滤器
                for (Filter filter : this.context.getFilters()) {
                    Object filterResult = filter.postFilter(rpcRequest, rpcResponse, result);
                    if (null != filterResult) {
                        return filterResult;
                    }
                }
                return result;
            } catch (RuntimeException ex) {
                // 如果非超时异常 直接退出循环
                if (!(ex.getCause() instanceof SocketTimeoutException)) {
                    throw ex;
                }
            }
        }

        return null;
    }

    private void isolate(InstanceMeta instance) {
        log.debug(" ===> isolate instance: {}" , instance);
        providers.remove(instance);
        log.debug(" ===> providers: {} ", providers);
        isolateProviders.add(instance);
        log.debug(" ===> isolatedProviders = {}", isolateProviders);
    }

    @Nullable
    private static Object caseReturnResult(Method method, RpcResponse<?> rpcResponse) {
        if (rpcResponse.getCode() != null && rpcResponse.getCode().equals(200)) {
            return TypeUtils.castMethodResult(method, rpcResponse.getData());
        } else {
            Exception exception = rpcResponse.getEx();
            if (exception instanceof RpcException ex) {
                throw ex;
            }
            throw new RpcException(rpcResponse.getEx(), RpcException.UNKNOWN_EX);
        }
    }

}
