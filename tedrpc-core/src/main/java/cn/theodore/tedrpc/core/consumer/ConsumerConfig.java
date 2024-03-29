package cn.theodore.tedrpc.core.consumer;

import cn.theodore.tedrpc.core.api.Filter;
import cn.theodore.tedrpc.core.api.LoadBalancer;
import cn.theodore.tedrpc.core.api.RegistryCenter;
import cn.theodore.tedrpc.core.api.Router;
import cn.theodore.tedrpc.core.cluster.RoundRibonLoadBalancer;
import cn.theodore.tedrpc.core.filter.CacheFilter;
import cn.theodore.tedrpc.core.filter.MockFilter;
import cn.theodore.tedrpc.core.meta.InstanceMeta;
import cn.theodore.tedrpc.core.registry.ZkRegistryCenter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

/**
 * @author linkuan
 */
@Configuration
@Slf4j
public class ConsumerConfig {

    @Value("${tedrpc.providers}")
    private String servers;

    @Bean
    public ConsumerBootStrap consumerBootStrap() {
        return new ConsumerBootStrap();
    }

    @Bean
    @Order(Integer.MIN_VALUE)
    public ApplicationRunner consumerBootStrap_runner(@Autowired ConsumerBootStrap consumerBootStrap) {
       return x -> {
           log.info("consumerBootStrap start");
           consumerBootStrap.start();
       };
    }

    @Bean
    public LoadBalancer<InstanceMeta> loadBalancer() {
//        return LoadBalancer.Default;
        return new RoundRibonLoadBalancer();
    }

    @Bean
    public Router<InstanceMeta> router() {
        return Router.Default;
    }

    /**
     * 启动方法
     * 停止方法
     * @return
     */
    @Bean(initMethod = "start", destroyMethod = "stop")
    public RegistryCenter consumer_rc() {
        return new ZkRegistryCenter();
    }

//    @Bean
//    public Filter filter() {
//        return new CacheFilter();
//    }

    @Bean
    public Filter filter2() {
        return new MockFilter();
    }
}
