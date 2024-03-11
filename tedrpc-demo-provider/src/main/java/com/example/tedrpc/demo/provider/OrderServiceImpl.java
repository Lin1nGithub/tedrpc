package com.example.tedrpc.demo.provider;

import cn.theodore.tedrpc.core.annotation.TedProvider;
import cn.theodore.tedrpc.demo.api.Order;
import cn.theodore.tedrpc.demo.api.OrderService;
import cn.theodore.tedrpc.demo.api.User;
import org.springframework.stereotype.Component;

import java.util.Random;

/**
 * @author linkuan
 */
@Component
@TedProvider
public class OrderServiceImpl implements OrderService {

    Random random = new Random(20);

    @Override
    public Order findById(Integer id) {

        // 模拟调用失败
        if (id == 404) {
            throw new RuntimeException("404 exception");
        }

        return new Order(id, random.nextLong());
    }
}
