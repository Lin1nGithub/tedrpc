package com.example.tedrpc.demo.provider;

import cn.theodore.tedrpc.core.annotation.TedProvider;
import cn.theodore.tedrpc.demo.api.User;
import cn.theodore.tedrpc.demo.api.UserService;
import org.springframework.stereotype.Component;

/**
 * @author linkuan
 */
@Component
@TedProvider
public class UserServiceImpl implements UserService {

    @Override
    public User findById(Integer id) {
        return new User(id, "ted-" + System.currentTimeMillis());
    }
}
