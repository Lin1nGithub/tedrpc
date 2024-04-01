package com.example.tedrpc.demo.provider;

import cn.theodore.tedrpc.core.test.TestZkServer;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest(classes = TedrpcDemoProviderApplication.class)
@Slf4j
class TedrpcDemoProviderApplicationTests {

	static TestZkServer zkServer = new TestZkServer();

	@BeforeAll
	static void init() {
		zkServer.start();
	}

	@Test
	void contextLoads() {
		log.info("====> TedrpcDemoProviderApplicationTests");
	}

	@AfterAll
	static void destroy(){
		zkServer.stop();
	}

}
