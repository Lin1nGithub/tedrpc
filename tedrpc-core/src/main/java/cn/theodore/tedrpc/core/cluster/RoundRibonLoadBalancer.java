package cn.theodore.tedrpc.core.cluster;

import cn.theodore.tedrpc.core.api.LoadBalancer;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 轮训调用 weightRR-权重算法,AAWR-自适应
 * 8081 w=100 25次
 * 8082 w=300 75次
 *
 *  todo 学习自适应负载均衡算法
 *       权重算法
 * @author linkuan
 */
public class RoundRibonLoadBalancer implements LoadBalancer {

    // todo increment 超过上限
    AtomicInteger index = new AtomicInteger(0);

    @Override
    public String choose(List<String> providers) {
        if ((providers == null || providers.isEmpty())) {
            return null;
        }
        if (providers.size() == 1) {
            return providers.get(0);
        }
        return providers.get((index.getAndIncrement()&0x7fffffff) % providers.size());
    }
}
