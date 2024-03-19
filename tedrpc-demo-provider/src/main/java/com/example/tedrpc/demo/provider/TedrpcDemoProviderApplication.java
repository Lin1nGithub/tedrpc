package com.example.tedrpc.demo.provider;

import cn.theodore.tedrpc.core.api.RpcRequest;
import cn.theodore.tedrpc.core.api.RpcResponse;
import cn.theodore.tedrpc.core.provider.ProviderBootstrap;
import cn.theodore.tedrpc.core.provider.ProviderConfig;
import jakarta.annotation.Resource;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
@Import({ProviderConfig.class})
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
            request.setMethodSign("findById@1_int");
            request.setArgs(new Object[]{100});

            RpcResponse rpcResponse = invoke(request);
            System.out.println("return :" + rpcResponse.getData());

            RpcRequest request1 = new RpcRequest();
            request1.setService("cn.theodore.tedrpc.demo.api.UserService");
            request1.setMethodSign("findById@2_int_java.lang.String");
            request1.setArgs(new Object[]{200,"cc"});

            RpcResponse rpcResponse1 = invoke(request1);
            System.out.println("return :" + rpcResponse1.getData());
        };
    }
}
