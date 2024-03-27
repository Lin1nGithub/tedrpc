package cn.theodore.tedrpc.core.api;

/**
 * @author linkuan
 */
public interface Filter {

    /**
     * 前置处理
     * @param request
     * @return
     */
    Object preFilter(RpcRequest request);

    Object postFilter(RpcRequest request, RpcResponse response, Object result);

    // Filter next();

    Filter Default = new Filter() {
        @Override
        public RpcResponse preFilter(RpcRequest request) {
            return null;
        }

        @Override
        public RpcResponse postFilter(RpcRequest request, RpcResponse response, Object result) {
            return null;
        }
    };
}
