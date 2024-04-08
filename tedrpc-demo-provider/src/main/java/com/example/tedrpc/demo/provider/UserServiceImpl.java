package com.example.tedrpc.demo.provider;

import cn.theodore.tedrpc.core.annotation.TedProvider;
import cn.theodore.tedrpc.demo.api.User;
import cn.theodore.tedrpc.demo.api.UserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * @author linkuan
 */
@Component
@TedProvider
@Slf4j
public class UserServiceImpl implements UserService {

    @Resource
    private Environment environment;

    @Override
    public User findById(int id) {
        return new User(id, "ted-V1-" + environment.getProperty("server.port") + "_" + System.currentTimeMillis());
    }

    @Override
    public User findById(int id, String name) {
        return new User(id, name);
    }

    @Override
    public String getName() {
        return "cc";
    }

    @Override
    public String getName(int id) {
        return "cc-" + id;
    }

    @Override
    public long findById(long id) {
        return id;
    }

    @Override
    public User getId(User user) {
        return user;
    }

    @Override
    public int[] getIds() {
        return new int[]{1, 2, 3};
    }

    @Override
    public int[] getIds(int[] ids) {
        return ids;
    }

    String timeoutPorts = "8081,8094";

    @Override
    public User ex(boolean flag) {
        if (flag) throw new RuntimeException("just throw an exception");
        return new User(100, "ted100");
    }

    @Override
    public User find(int timeout) {
        log.info("find timetout:{}", timeout);
        String port = environment.getProperty("server.port");
        if (Arrays.stream(timeoutPorts.split(",")).anyMatch(port::equals)) {
            try {
                Thread.sleep(timeout);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return new User(1001, "TED1001-" + port);
    }

    public void setTimeoutPorts(String timeoutPorts) {
        this.timeoutPorts = timeoutPorts;
    }


}
