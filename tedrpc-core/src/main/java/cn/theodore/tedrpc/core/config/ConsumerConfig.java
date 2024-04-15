package cn.theodore.tedrpc.core.config;

import cn.theodore.tedrpc.core.api.*;
import cn.theodore.tedrpc.core.cluster.GrayRouter;
import cn.theodore.tedrpc.core.cluster.RoundRibonLoadBalancer;
import cn.theodore.tedrpc.core.consumer.ConsumerBootStrap;
import cn.theodore.tedrpc.core.meta.InstanceMeta;
import cn.theodore.tedrpc.core.registry.zk.ZkRegistryCenter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.List;

/**
 * @author linkuan
 */
@Slf4j
@Configuration
@Import({AppConfigProperties.class,ConsumerConfigProperties.class})
public class ConsumerConfig {

    @Autowired
    private AppConfigProperties appConfigProperties;

    @Autowired
    private ConsumerConfigProperties consumerConfigProperties;

    @Bean
    public ConsumerBootStrap createConsumerBootstrap() {
        return new ConsumerBootStrap();
    }

    @Bean
    public ApplicationRunner consumerBootstrap_runner(@Autowired ConsumerBootStrap consumerBootStrap) {
        return x -> {
            log.info("consumerBootStrap starting ...");
            consumerBootStrap.start();
            log.info("consumerBootStrap started ...");
        };
    }

    @Bean
    public LoadBalancer<InstanceMeta> loadBalancer() {
        return new RoundRibonLoadBalancer<>();
    }

    @Bean
    public Router<InstanceMeta> router() {
        return new GrayRouter(consumerConfigProperties.getGrayRatio());
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    @ConditionalOnMissingBean
    public RegistryCenter consumer_rc() {
        return new ZkRegistryCenter();
    }

    @Bean
    public RpcContext createContext(@Autowired Router router,
                                    @Autowired LoadBalancer loadBalancer,
                                    @Autowired List<Filter> filters) {
        RpcContext context = new RpcContext();
        context.setRouter(router);
        context.setLoadBalancer(loadBalancer);
        context.setFilters(filters);
        context.getParameters().put("app.id", appConfigProperties.getId());
        context.getParameters().put("app.namespace", appConfigProperties.getNamespace());
        context.getParameters().put("app.env", appConfigProperties.getEnv());
        context.getParameters().put("consumer.retries", String.valueOf(consumerConfigProperties.getRetries()));
        context.getParameters().put("consumer.timeout", String.valueOf(consumerConfigProperties.getTimeout()));
        context.getParameters().put("consumer.faultLimit", String.valueOf(consumerConfigProperties.getFaultLimit()));
        context.getParameters().put("consumer.halfOpenInitialDelay", String.valueOf(consumerConfigProperties.getHalfOpenInitialDelay()));
        context.getParameters().put("consumer.halfOpenDelay", String.valueOf(consumerConfigProperties.getHalfOpenDelay()));
        return context;
    }
}
