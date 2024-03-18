package cn.theodore.tedrpc.core.consumer;

import cn.theodore.tedrpc.core.api.LoadBalancer;
import cn.theodore.tedrpc.core.api.Router;
import cn.theodore.tedrpc.core.cluster.RandomLoadBalancer;
import cn.theodore.tedrpc.core.cluster.RoundRibonLoadBalancer;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

/**
 * @author linkuan
 */
@Configuration
public class ConsumerConfig {

    @Bean
    public ConsumerBootStrap consumerBootStrap() {
        return new ConsumerBootStrap();
    }

    @Bean
    @Order(Integer.MIN_VALUE)
    public ApplicationRunner consumerBootStrap_runner(@Autowired ConsumerBootStrap consumerBootStrap) {
       return x -> {
           System.out.println("consumerBootStrap start");
           consumerBootStrap.start();
       };
    }

    @Bean
    public LoadBalancer loadBalancer() {
//        return LoadBalancer.Default;
        return new RoundRibonLoadBalancer();
    }

    @Bean
    public Router router() {
        return Router.Default;
    }
}
