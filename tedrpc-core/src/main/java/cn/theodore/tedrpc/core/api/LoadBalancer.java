package cn.theodore.tedrpc.core.api;

import java.util.List;

/**
 * 轮训调用 weightRR-权重算法,AAWR-自适应
 * 8081 w=100 25次
 * 8082 w=300 75次
 *
 *  todo 学习自适应负载均衡算法
 *       权重算法
 * @author linkuan
 */
public interface LoadBalancer<T> {

    T choose(List<T> providers);


    /**
     * 默认实现
     */
    LoadBalancer Default = providers -> (providers == null || providers.isEmpty()) ? null : providers.get(0);
}
