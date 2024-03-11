package cn.theodore.tedrpc.demo.consumer;

import cn.theodore.tedrpc.core.annotation.TedConsumer;
import cn.theodore.tedrpc.core.consumer.ConsumerBootStrap;
import cn.theodore.tedrpc.core.consumer.ConsumerConfig;
import cn.theodore.tedrpc.demo.api.Order;
import cn.theodore.tedrpc.demo.api.OrderService;
import cn.theodore.tedrpc.demo.api.User;
import cn.theodore.tedrpc.demo.api.UserService;
import jakarta.annotation.Resource;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({ConsumerConfig.class})
public class TedrpcDemoConsumerApplication {

    @TedConsumer
    private UserService userService;

    @TedConsumer
    private OrderService orderService;

    @Resource
    private ConsumerBootStrap consumerBootStrap;

    public static void main(String[] args) {
        SpringApplication.run(TedrpcDemoConsumerApplication.class, args);
    }

    // 为什么不能像provider 初始化
    // 1. 在所有的初始化完成之后
    // 2.

    @Bean
    public ApplicationRunner runner() {
        return x -> {
            User user = userService.findById(1);
            System.out.println("RPC result userService.findById= " + user);

            Order order = orderService.findById(404);
            System.out.println("RPC result orderService.findById= " + order);
        };
    }

}
