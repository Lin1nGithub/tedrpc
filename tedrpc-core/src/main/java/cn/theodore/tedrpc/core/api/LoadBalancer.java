package cn.theodore.tedrpc.core.api;

import java.util.List;

/**
 * @author linkuan
 */
public interface LoadBalancer {

    String choose(List<String> providers);


    /**
     * 默认实现
     */
    LoadBalancer Default = providers -> (providers == null || providers.isEmpty()) ? null : providers.get(0);
}
