package cn.theodore.tedrpc.demo.consumer;

import cn.theodore.tedrpc.core.annotation.TedConsumer;
import cn.theodore.tedrpc.demo.api.User;
import cn.theodore.tedrpc.demo.api.UserService;
import org.springframework.stereotype.Component;

/**
 * @author linkuan
 */
@Component
public class Demo {

    @TedConsumer
    private UserService userService2;

    public void test1() {
        User user = userService2.findById(100);
        System.out.println(user);
    }

}
