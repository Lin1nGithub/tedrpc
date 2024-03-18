package cn.theodore.tedrpc.core.cluster;

import cn.theodore.tedrpc.core.api.LoadBalancer;

import java.util.List;
import java.util.Random;

/**
 * 随机调用
 * @author linkuan
 */
public class RandomLoadBalancer implements LoadBalancer {

    private Random random = new Random();

    @Override
    public String choose(List<String> providers) {
        if ((providers == null || providers.isEmpty())) {
            return null;
        }
        if (providers.size() == 1) {
            return providers.get(0);
        }
        return providers.get(random.nextInt(providers.size()));
    }
}
