package cn.theodore.tedrpc.core.consumer;

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
}
