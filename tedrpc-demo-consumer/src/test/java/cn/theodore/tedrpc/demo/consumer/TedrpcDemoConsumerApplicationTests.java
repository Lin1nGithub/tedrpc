package cn.theodore.tedrpc.demo.consumer;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

/**
 * consumer的单测依赖provider
 */
@SpringBootTest
@Slf4j
class TedrpcDemoConsumerApplicationTests {

    static ApplicationContext context;

    @BeforeAll
    static void init() {
        context = SpringApplication.run(TedrpcDemoConsumerApplication.class,"--server.port=8084");
    }

    @Test
    void contextLoads() {
        log.info("=====> aaaaaaaaaaaaaa");
    }

    @AfterAll
    static void destroy(){
        SpringApplication.exit(context, () -> 1);
    }


}
