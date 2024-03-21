package cn.theodore.tedrpc.core.consumer;

import cn.theodore.tedrpc.core.api.RpcRequest;
import cn.theodore.tedrpc.core.api.RpcResponse;
import com.alibaba.fastjson.JSON;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author linkuan
 */
public class OkHttpInvoker implements HttpInvoker {

    private OkHttpClient client;

    private final static MediaType JSONTYPE = MediaType.get("application/json;charset=utf-8");

    public OkHttpInvoker() {
        client = new OkHttpClient.Builder()
//            .connectionPool(new ConnectionPool(16, 60, TimeUnit.SECONDS))
                .readTimeout(1, TimeUnit.SECONDS)
                .writeTimeout(1, TimeUnit.SECONDS)
                .connectTimeout(1, TimeUnit.SECONDS)
                .build();
    }


    @Override
    public RpcResponse<?> post(RpcRequest rpcRequest, String url) {
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
