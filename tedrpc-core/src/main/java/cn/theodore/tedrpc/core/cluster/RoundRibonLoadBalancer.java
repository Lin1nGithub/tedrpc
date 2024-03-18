package cn.theodore.tedrpc.core.cluster;

import cn.theodore.tedrpc.core.api.LoadBalancer;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 轮训调用
 * @author linkuan
 */
public class RoundRibonLoadBalancer<T> implements LoadBalancer<T> {

    // todo increment 超过上限
    AtomicInteger index = new AtomicInteger(0);

    @Override
    public T choose(List<T> providers) {
        if ((providers == null || providers.isEmpty())) {
            return null;
        }
        if (providers.size() == 1) {
            return providers.get(0);
        }
        // & 0x7fffffff 防止 AtomicInteger越界
        return providers.get((index.getAndIncrement()) % providers.size());
    }
}
