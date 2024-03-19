package cn.theodore.tedrpc.core.api;

import java.util.List;

/**
 * 轮训调用 weightRR-权重算法,AAWR-自适应
 * 8081 w=100 25次
 * 8082 w=300 75次
 *
 *  todo 学习自适应负载均衡算法 P2C算法
 *       权重算法
 *       在并发不高的时候 RR和轮训算法的效果和其他复杂算法是一样的
 * @author linkuan
 */
public interface LoadBalancer<T> {

    T choose(List<T> providers);


    /**
     * 默认实现
     */
    LoadBalancer Default = providers -> (providers == null || providers.isEmpty()) ? null : providers.get(0);
}
