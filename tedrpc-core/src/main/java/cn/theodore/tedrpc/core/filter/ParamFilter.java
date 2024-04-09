package cn.theodore.tedrpc.core.filter;

import cn.theodore.tedrpc.core.api.Filter;
import cn.theodore.tedrpc.core.api.RpcContext;
import cn.theodore.tedrpc.core.api.RpcRequest;
import cn.theodore.tedrpc.core.api.RpcResponse;

import java.util.Map;

/**
 * 通过Filter的方式,将上下文参数赋值到RpcRequest对象中
 * @author linkuan
 */

public class ParamFilter implements Filter {



    @Override
    public Object preFilter(RpcRequest request) {
        Map<String, String> params = RpcContext.ContextParameters.get();
        if (!params.isEmpty()) {
            request.getParams().putAll(params);
        }
        return null;
    }

    @Override
    public Object postFilter(RpcRequest request, RpcResponse response, Object result) {
        RpcContext.ContextParameters.get().clear();
        return null;
    }
}
