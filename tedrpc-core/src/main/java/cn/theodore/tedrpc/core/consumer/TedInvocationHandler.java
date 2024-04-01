package cn.theodore.tedrpc.core.consumer;

import cn.theodore.tedrpc.core.api.Filter;
import cn.theodore.tedrpc.core.api.RpcContext;
import cn.theodore.tedrpc.core.api.RpcRequest;
import cn.theodore.tedrpc.core.api.RpcResponse;
import cn.theodore.tedrpc.core.consumer.http.OkHttpInvoker;
import cn.theodore.tedrpc.core.meta.InstanceMeta;
import cn.theodore.tedrpc.core.util.MethodUtils;
import cn.theodore.tedrpc.core.util.TypeUtils;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;

/**
 * 消费端的动态代理处理类.
 *
 * @author linkuan
 */
@Slf4j
public class TedInvocationHandler implements InvocationHandler {

    private Class<?> service;

    private RpcContext context;

    private List<InstanceMeta> providers;

    private HttpInvoker httpInvoker = new OkHttpInvoker();

    public TedInvocationHandler(Class<?> clz, RpcContext context, List<InstanceMeta> providers) {
        this.service = clz;
        this.context = context;
        this.providers = providers;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        if (MethodUtils.checkLocalMethod(method)) {
            return null;
        }

        RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.setService(service.getCanonicalName());
        rpcRequest.setMethodSign(MethodUtils.methodSign(method));
        rpcRequest.setArgs(args);

        // 请求前的过滤
        for (Filter filter : this.context.getFilters()) {
            Object preResult = filter.preFilter(rpcRequest);
            if (null != preResult) {
                log.info(filter.getClass().getName() + "====> preFilter" + preResult);
                return preResult;
            }
        }

        List<InstanceMeta> instances = context.getRouter().route(providers);
        InstanceMeta instance = context.getLoadBalancer().choose(instances);
        log.info("loadBalancer.choose(nodes) ==> " + instance);
        RpcResponse<?> rpcResponse = httpInvoker.post(rpcRequest, instance.toUrl());
        Object result = caseReturnResult(method, rpcResponse);

        // 请求后的过滤器
        for (Filter filter : this.context.getFilters()) {
            Object filterResult = filter.postFilter(rpcRequest, rpcResponse, result);
            if (null != filterResult) {
                return filterResult;
            }
        }

        return result;
    }

    @Nullable
    private static Object caseReturnResult(Method method, RpcResponse<?> rpcResponse) {
        if (rpcResponse.getCode() != null && rpcResponse.getCode().equals(200)) {
            return TypeUtils.castMethodResult(method, rpcResponse.getData());
        } else {
            // ex.printStackTrace();
            throw new RuntimeException(rpcResponse.getEx());
        }
    }

}
