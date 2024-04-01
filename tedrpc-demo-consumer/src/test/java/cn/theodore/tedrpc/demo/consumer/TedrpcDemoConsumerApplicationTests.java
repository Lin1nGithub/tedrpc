package cn.theodore.tedrpc.demo.consumer;

import cn.theodore.tedrpc.core.test.TestZkServer;
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
@SpringBootTest(classes = {TedrpcDemoConsumerApplication.class})
@Slf4j
class TedrpcDemoConsumerApplicationTests {

    static ApplicationContext context;

    static TestZkServer zkServer = new TestZkServer();

    @BeforeAll
    static void init() {

        log.info(" =============================== ");
        log.info(" =============================== ");
        log.info(" =============================== ");
        log.info(" =============================== ");

        zkServer.start();

        context = SpringApplication.run(TedrpcDemoConsumerApplication.class,"--server.port=8084",
                "--kkrpc.zkServer=localhost:2181",
                "--logging.level.cn.theodore.tedrpc=debug");
    }

    @Test
    void contextLoads() {
        log.info("=====> aaaaaaaaaaaaaa");
    }

    @AfterAll
    static void destroy(){
        SpringApplication.exit(context, () -> 1);
        zkServer.stop();
    }


}
