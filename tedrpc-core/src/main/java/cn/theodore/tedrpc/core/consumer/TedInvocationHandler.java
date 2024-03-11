package cn.theodore.tedrpc.core.consumer;

import cn.theodore.tedrpc.core.api.RpcRequest;
import cn.theodore.tedrpc.core.api.RpcResponse;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import okhttp3.*;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * @author linkuan
 */
public class TedInvocationHandler implements InvocationHandler {

    private final static MediaType JSONTYPE = MediaType.get("application/json;charset=utf-8");

    private Class<?> service;

    public TedInvocationHandler(Class<?> clz) {
        this.service = clz;
    }
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.setService(service.getCanonicalName());
        rpcRequest.setMethod(method.getName());
        rpcRequest.setArgs(args);

        RpcResponse rpcResponse = post(rpcRequest);
        if (rpcResponse.getCode() != null && rpcResponse.getCode().equals(200)) {
            JSONObject jsonResult = (JSONObject) rpcResponse.getData();
            return jsonResult.toJavaObject(method.getReturnType());
        }else {
            Exception ex = rpcResponse.getEx();
            // ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    OkHttpClient client = new OkHttpClient.Builder()
//            .connectionPool(new ConnectionPool(16, 60, TimeUnit.SECONDS))
            .readTimeout(1, TimeUnit.SECONDS)
            .writeTimeout(1, TimeUnit.SECONDS)
            .connectTimeout(1, TimeUnit.SECONDS)
            .build();

    private RpcResponse post(RpcRequest rpcRequest) {
        String reqJson = JSON.toJSONString(rpcRequest);
        Request request = new Request.Builder()
                .url("http://localhost:8080/")
                .post(RequestBody.create(reqJson, JSONTYPE))
                .build();
        try {
            String respJson = client.newCall(request).execute().body().string();
            System.out.println("====> respJson " + respJson);
            RpcResponse rpcResponse = JSON.parseObject(respJson, RpcResponse.class);
            return rpcResponse;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
