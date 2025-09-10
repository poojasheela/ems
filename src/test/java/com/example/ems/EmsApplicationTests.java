package com.example.ems;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
		"spring.cloud.vault.enabled=false"
})
class EmsApplicationTests {

	@Test
	void contextLoads() {

	}
}
