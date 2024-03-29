package cn.theodore.tedrpc.core.api;

import cn.theodore.tedrpc.core.meta.InstanceMeta;
import lombok.Data;

import java.util.List;

/**
 * rpc上下文
 * @author linkuan
 */
@Data
public class RpcContext {

    /**
     * 过滤器
     */
    List<Filter> filters;

    /**
     * 路由
     */
    private Router<InstanceMeta> router;

    /**
     * 均衡负载
     */
    private LoadBalancer<InstanceMeta> loadBalancer;
}
