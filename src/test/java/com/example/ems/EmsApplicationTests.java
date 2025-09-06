package com.example.ems;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
		"spring.cloud.vault.enabled=false" // ⛔ disable Vault in tests
})
class EmsApplicationTests {

	@Test
	void contextLoads() {
		// ✅ passes if the Spring ApplicationContext loads correctly
	}
}
