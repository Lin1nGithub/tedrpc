package com.example.tedrpc.demo.provider;

import cn.theodore.tedrpc.core.annotation.TedProvider;
import cn.theodore.tedrpc.core.api.RpcRequest;
import cn.theodore.tedrpc.core.api.RpcResponse;
import cn.theodore.tedrpc.core.provider.ProviderBootstrap;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
@RestController
@Import({ProviderBootstrap.class})
public class TedrpcDemoProviderApplication {

    public static void main(String[] args) {
        SpringApplication.run(TedrpcDemoProviderApplication.class, args);
    }

    @Resource
    private ProviderBootstrap providerBootstrap;

    @RequestMapping("/")
    // 使用HTTP + JSON 实现序列化和通讯
    public RpcResponse invoke(@RequestBody RpcRequest request) {
        return providerBootstrap.invoke(request);
    }

    @Bean
    public ApplicationRunner providerRun() {
        return x -> {
            RpcRequest request = new RpcRequest();
            request.setService("cn.theodore.tedrpc.demo.api.UserService");
            request.setMethod("findById");
            request.setArgs(new Object[]{100});

            RpcResponse rpcResponse = invoke(request);
            System.out.println("return :" + rpcResponse.getData());

            request.setService("cn.theodore.tedrpc.demo.api.OrderService");
            request.setMethod("findById");
            request.setArgs(new Object[]{100});
            rpcResponse = invoke(request);
            System.out.println("return :" + rpcResponse.getData());
        };
    }
}
