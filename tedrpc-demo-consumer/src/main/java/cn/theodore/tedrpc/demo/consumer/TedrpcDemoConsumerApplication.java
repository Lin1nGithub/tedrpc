package cn.theodore.tedrpc.demo.consumer;

import cn.theodore.tedrpc.core.annotation.TedConsumer;
import cn.theodore.tedrpc.core.api.Router;
import cn.theodore.tedrpc.core.api.RpcContext;
import cn.theodore.tedrpc.core.api.RpcResponse;
import cn.theodore.tedrpc.core.cluster.GrayRouter;
import cn.theodore.tedrpc.core.config.ConsumerConfig;
import cn.theodore.tedrpc.core.consumer.ConsumerBootStrap;
import cn.theodore.tedrpc.demo.api.User;
import cn.theodore.tedrpc.demo.api.UserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@Import({ConsumerConfig.class})
@RestController
@Slf4j
public class TedrpcDemoConsumerApplication {

    @TedConsumer
    private UserService userService;

//    @TedConsumer
//    private OrderService orderService;

    @Resource
    private ConsumerBootStrap consumerBootStrap;

//    @Resource
//    private Demo demo;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public User findById(int id) {
        return userService.findById(id);
    }

    @RequestMapping("/ports")
    public RpcResponse<String> ports(@RequestParam("ports") String ports) {
        userService.setTimeoutPorts(ports);
        RpcResponse<String> response = new RpcResponse<>();
        response.setCode(200);
        response.setData("OK:" + ports);
        return response;
    }

    @Autowired
    private Router grayRouter;

    @RequestMapping("/gray/")
    public String gray(@RequestParam("ratio") int ratio) {
        ((GrayRouter)grayRouter).setGrayRatio(ratio);
        return "OK-new gray ratio is " + ratio;
    }


    public static void main(String[] args) {
        SpringApplication.run(TedrpcDemoConsumerApplication.class, args);
    }

    // 为什么不能像provider 初始化
    // 1. 在所有的初始化完成之后
    // 2.

    @Bean
    public ApplicationRunner runner() {

        // 超时设置的【漏斗原则】
        // A 2000 => B 1500 => 1200 => 1000

        return x -> {

//            // 报错原因: 微服务返回的10 自动转换成Integer类型
//            // 强转为返回值要求的Long类型报错。
//            // 解决方法: consumer端的TedInvocationHandler中进行参数转换
//            Long id = userService.findById(10L);
//            log.info("RPC result userService.findById(10) ==> " + id);
//
//
//            // 报错原因: provider端, method中要求的参数类型是User对象,
//            // 但是通过微服务调用过去的是LinkedHashMap类型, 强转类型失败。
//            // 解决方法: provider的method.invoke前需要进行参数类型转换
//            User user = new User();
//            user.setId(100);
//            user.setName("cc");
//            log.info("RPC result userService.getId(user) ==> " + userService.getId(user));
//
//            User user1 = userService.findById(1,"hubao");
//            log.info("RPC result userService.findById findById(1,\"hubao\")" + user1);
//
            log.info("RPC result userService.getName() ===> " + userService.getName());
//
//            log.info(userService.getName(123));

//            log.info("RPC result userService.getIds() ==> ");
//            int[] ids = userService.getIds();
//            for (int i : ids) {
//                log.info("" + i);
//            }
//
//            log.info("RPC result userService.getIds(new int[]{3, 4, 5}) ==> ");
//            int[] ids1 = userService.getIds(new int[]{3, 4, 5});
//            for (int i : ids1) {
//                log.info("" + i);
//            }

            // 可以调用远程 到toString方法 类似的还有 hashCode
//            String string = userService.toString();
//            int i = userService.hashCode();

//            Order order = orderService.findById(2);
//            log.info("RPC result orderService.findById= " + order);
//
//

//            int id = userService.getId(1);


            // demo.test1();;

//            System.out.println("Case 17. >>===[测试服务端抛出一个RuntimeException异常]===");
//            try {
//                long start = System.currentTimeMillis();
//                User userEx = userService.ex(true);
//                log.info("userService.ex" + (System.currentTimeMillis() - start) + "ms");
//            } catch (RuntimeException e) {
//                log.info(" ===> exception: " + e.getMessage());
//            }

            long start = System.currentTimeMillis();
            userService.find(1000);
            log.info("userService.find take " + (System.currentTimeMillis() - start) + "ms");

            log.info("Case 19. >>===[测试通过Context跨消费者和提供者进行传参]===");
            String Key_Version = "rpc.version";
            String Key_Message = "rpc.message";
            RpcContext.setContextParameter(Key_Version, "V8");
            RpcContext.setContextParameter(Key_Message, "this is a v8 message");
            String version = userService.echoParameter(Key_Version);
            RpcContext.setContextParameter(Key_Version, "V9");
            RpcContext.setContextParameter(Key_Message, "this is a v9 message");
            String message = userService.echoParameter(Key_Message);
            log.info(" ===> echo parameter from c->p->c: " + Key_Version + " -> " + version);
            log.info(" ===> echo parameter from c->p->c: " + Key_Message + " -> " + message);
            RpcContext.ContextParameters.get().clear(); // 当前线程被复用, 第二次请求过来,带上了上一次请求的参数
        };
    }

}
