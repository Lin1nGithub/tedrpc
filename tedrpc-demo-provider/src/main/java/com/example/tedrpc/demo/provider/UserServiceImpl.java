package com.example.tedrpc.demo.provider;

import cn.theodore.tedrpc.core.annotation.TedProvider;
import cn.theodore.tedrpc.demo.api.User;
import cn.theodore.tedrpc.demo.api.UserService;
import jakarta.annotation.Resource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * @author linkuan
 */
@Component
@TedProvider
public class UserServiceImpl implements UserService {

    @Resource
    private Environment environment;

    @Override
    public User findById(int id) {
        return new User(id, "ted-" + environment.getProperty("server.port") + "_" + System.currentTimeMillis());
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
        if(flag) throw new RuntimeException("just throw an exception");
        return new User(100, "ted100");
    }

    public void setTimeoutPorts(String timeoutPorts) {
        this.timeoutPorts = timeoutPorts;
    }


}
