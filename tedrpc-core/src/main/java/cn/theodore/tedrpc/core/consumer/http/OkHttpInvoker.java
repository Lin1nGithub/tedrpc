package cn.theodore.tedrpc.core.consumer.http;

import cn.theodore.tedrpc.core.api.RpcRequest;
import cn.theodore.tedrpc.core.api.RpcResponse;
import cn.theodore.tedrpc.core.consumer.HttpInvoker;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author linkuan
 */
@Slf4j
public class OkHttpInvoker implements HttpInvoker {

    private OkHttpClient client;

    private final static MediaType JSONTYPE = MediaType.get("application/json;charset=utf-8");

    public OkHttpInvoker(int timeout) {
        client = new OkHttpClient.Builder()
//            .connectionPool(new ConnectionPool(16, 60, TimeUnit.SECONDS))
                .readTimeout(timeout, TimeUnit.MILLISECONDS)
                .writeTimeout(timeout, TimeUnit.MILLISECONDS)
                .connectTimeout(timeout, TimeUnit.MILLISECONDS)
                .build();
    }


    @Override
    public RpcResponse<?> post(RpcRequest rpcRequest, String url) {
        String reqJson = JSON.toJSONString(rpcRequest);
        log.info(" ===> reqJson = " + reqJson);
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(reqJson, JSONTYPE))
                .build();
        try {
            String respJson = client.newCall(request).execute().body().string();
            log.info("====> respJson " + respJson);
            RpcResponse rpcResponse = JSON.parseObject(respJson, RpcResponse.class);
            return rpcResponse;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
