package com.playkuround.demo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "spring.profiles.active=test")
class HealthCheckApplicationTests {

	@Test
	void contextLoads() {
	}

}
