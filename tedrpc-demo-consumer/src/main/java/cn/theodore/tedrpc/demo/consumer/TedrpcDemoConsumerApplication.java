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

    @Resource
    private Demo demo;

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
            System.out.println("RPC result userService.findById(1) " + user);

            User user1 = userService.findById(1,"hubao");
            System.out.println("RPC result userService.findById findById(1,\"hubao\")" + user1);

            System.out.println(userService.getName());

            System.out.println(userService.getName(123));

            // 可以调用远程 到toString方法 类似的还有 hashCode
//            String string = userService.toString();
//            int i = userService.hashCode();

//            Order order = orderService.findById(2);
//            System.out.println("RPC result orderService.findById= " + order);
//
//

//            int id = userService.getId(1);


            // demo.test1();;
        };
    }

}
