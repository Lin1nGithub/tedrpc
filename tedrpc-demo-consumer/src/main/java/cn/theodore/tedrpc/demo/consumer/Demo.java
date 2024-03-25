package cn.theodore.tedrpc.demo.consumer;

import cn.theodore.tedrpc.core.annotation.TedConsumer;
import cn.theodore.tedrpc.demo.api.User;
import cn.theodore.tedrpc.demo.api.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author linkuan
 */
@Component
@Slf4j
public class Demo {

    @TedConsumer
    private UserService userService2;

    public void test1() {
        User user = userService2.findById(100);
        log.info("" + user);
    }

}
