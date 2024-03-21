package cn.theodore.tedrpc.core.consumer;

import cn.theodore.tedrpc.core.api.RpcRequest;
import cn.theodore.tedrpc.core.api.RpcResponse;

/**
 * @author linkuan
 */
public interface HttpInvoker {


    RpcResponse<?> post(RpcRequest rpcRequest, String url);

}
