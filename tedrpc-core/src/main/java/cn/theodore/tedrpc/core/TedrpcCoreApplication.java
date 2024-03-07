package cn.theodore.tedrpc.core;

import cn.theodore.tedrpc.core.provider.ProviderBootstrap;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
public class TedrpcCoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(TedrpcCoreApplication.class, args);
    }

}
