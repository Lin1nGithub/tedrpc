package cn.theodore.tedrpc.demo.consumer;

import cn.theodore.tedrpc.core.annotation.TedConsumer;
import cn.theodore.tedrpc.core.api.RpcRequest;
import cn.theodore.tedrpc.core.api.RpcResponse;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@SpringBootApplication
@Import({ConsumerConfig.class})
@RestController
public class TedrpcDemoConsumerApplication {

    @TedConsumer
    private UserService userService;

//    @TedConsumer
//    private OrderService orderService;

    @Resource
    private ConsumerBootStrap consumerBootStrap;

//    @Resource
//    private Demo demo;

    @RequestMapping(value = "/",method = RequestMethod.GET)
    public User findById(int id) {
        return userService.findById(id);
    }


    public static void main(String[] args) {
        SpringApplication.run(TedrpcDemoConsumerApplication.class, args);
    }

    // 为什么不能像provider 初始化
    // 1. 在所有的初始化完成之后
    // 2.

    @Bean
    public ApplicationRunner runner() {
        return x -> {

//            // 报错原因: 微服务返回的10 自动转换成Integer类型
//            // 强转为返回值要求的Long类型报错。
//            // 解决方法: consumer端的TedInvocationHandler中进行参数转换
//            Long id = userService.findById(10L);
//            System.out.println("RPC result userService.findById(10) ==> " + id);
//
//
//            // 报错原因: provider端, method中要求的参数类型是User对象,
//            // 但是通过微服务调用过去的是LinkedHashMap类型, 强转类型失败。
//            // 解决方法: provider的method.invoke前需要进行参数类型转换
//            User user = new User();
//            user.setId(100);
//            user.setName("cc");
//            System.out.println("RPC result userService.getId(user) ==> " + userService.getId(user));
//
//            User user1 = userService.findById(1,"hubao");
//            System.out.println("RPC result userService.findById findById(1,\"hubao\")" + user1);
//
//            System.out.println(userService.getName());
//
//            System.out.println(userService.getName(123));

            System.out.println("RPC result userService.getIds() ==> ");
            int[] ids = userService.getIds();
            for (int i : ids) {
                System.out.println(i);
            }

            System.out.println("RPC result userService.getIds(new int[]{3, 4, 5}) ==> ");
            int[] ids1 = userService.getIds(new int[]{3, 4, 5});
            for (int i : ids1) {
                System.out.println(i);
            }

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
