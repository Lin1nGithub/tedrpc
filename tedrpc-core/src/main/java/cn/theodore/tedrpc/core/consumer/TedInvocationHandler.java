package cn.theodore.tedrpc.core.consumer;

import cn.theodore.tedrpc.core.api.*;
import cn.theodore.tedrpc.core.util.MethodUtils;
import cn.theodore.tedrpc.core.util.TypeUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import okhttp3.*;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author linkuan
 */
public class TedInvocationHandler implements InvocationHandler {

    private final static MediaType JSONTYPE = MediaType.get("application/json;charset=utf-8");

    private Class<?> service;

    private RpcContext context;

    private List<String> providers;

    public TedInvocationHandler(Class<?> clz, RpcContext context, List<String> providers) {
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

        List<String> urls = context.getRouter().route(providers);
        String url = (String) context.getLoadBalancer().choose(urls);
        System.out.println("loadBalancer.choose(url) ==> " + url);
        RpcResponse rpcResponse = post(rpcRequest, url);

        if (rpcResponse.getCode() != null && rpcResponse.getCode().equals(200)) {
            Object data = rpcResponse.getData();
            if (data instanceof JSONObject) {
                JSONObject jsonResult = (JSONObject) rpcResponse.getData();
                return jsonResult.toJavaObject(method.getReturnType());
            }else if (data instanceof JSONArray jsonArray) {
                Object[] array = jsonArray.stream().toArray();
                Class<?> componentType = method.getReturnType().getComponentType();
                Object resultArray = Array.newInstance(componentType, array.length);
                for (int i = 0; i < array.length; i++) {
                    Array.set(resultArray,i, array[i]);
                }
                return resultArray;
            }else {
                return TypeUtils.castType(data, method.getReturnType());
            }
        }else {
            Exception ex = rpcResponse.getEx();
            // ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T convertObject(Object obj, Class<?> clz) {
        if (obj == null) {
            return null;
        }

        // 对基本类型的包装类进行判断和转换
        if (obj instanceof Integer || obj instanceof Double || obj instanceof Float ||
                obj instanceof Long || obj instanceof Short || obj instanceof Byte ||
                obj instanceof Boolean || obj instanceof Character) {
            return (T) obj; // 直接返回，利用自动装箱
        }

        // 如果有其他类型需要转换处理，可以在这里继续添加逻辑
        // 例如，如果你需要特殊处理String类型
        if (obj instanceof String) {
            return (T) obj; // 假设直接返回String对象
        }

        // 如果不符合以上任何条件，可以返回null或抛出异常
        return null;
    }

    OkHttpClient client = new OkHttpClient.Builder()
//            .connectionPool(new ConnectionPool(16, 60, TimeUnit.SECONDS))
            .readTimeout(1, TimeUnit.SECONDS)
            .writeTimeout(1, TimeUnit.SECONDS)
            .connectTimeout(1, TimeUnit.SECONDS)
            .build();

    private RpcResponse post(RpcRequest rpcRequest, String url) {
        String reqJson = JSON.toJSONString(rpcRequest);
        Request request = new Request.Builder()
                .url(url)
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
