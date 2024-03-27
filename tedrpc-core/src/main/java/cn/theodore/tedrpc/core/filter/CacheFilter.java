package cn.theodore.tedrpc.core.filter;

import cn.theodore.tedrpc.core.api.Filter;
import cn.theodore.tedrpc.core.api.RpcRequest;
import cn.theodore.tedrpc.core.api.RpcResponse;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author linkuan
 */
public class CacheFilter implements Filter {
    private static Map<String, Object> cache = new ConcurrentHashMap();

    @Override
    public Object preFilter(RpcRequest request) {
        return cache.get(request.toString());
    }

    @Override
    public Object postFilter(RpcRequest request, RpcResponse response, Object result) {
        cache.putIfAbsent(request.toString(), result);
        return result;
    }
}
