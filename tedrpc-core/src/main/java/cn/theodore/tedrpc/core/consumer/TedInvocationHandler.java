package cn.theodore.tedrpc.core.consumer;

import cn.theodore.tedrpc.core.api.RpcContext;
import cn.theodore.tedrpc.core.api.RpcRequest;
import cn.theodore.tedrpc.core.api.RpcResponse;
import cn.theodore.tedrpc.core.consumer.http.OkHttpInvoker;
import cn.theodore.tedrpc.core.meta.InstanceMeta;
import cn.theodore.tedrpc.core.util.MethodUtils;
import cn.theodore.tedrpc.core.util.TypeUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;

/**
 * 消费端的动态代理处理类.
 * @author linkuan
 */
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

        List<InstanceMeta> instances = context.getRouter().route(providers);
        InstanceMeta instance = context.getLoadBalancer().choose(instances);
        System.out.println("loadBalancer.choose(nodes) ==> " + instance);
        RpcResponse<?> rpcResponse = httpInvoker.post(rpcRequest, instance.toUrl());

        if (rpcResponse.getCode() != null && rpcResponse.getCode().equals(200)) {
            Object data = rpcResponse.getData();
            return TypeUtils.castMethodResult(method, data);
        }else {
            Exception ex = rpcResponse.getEx();
            // ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

}
