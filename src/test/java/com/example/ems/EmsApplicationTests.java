package com.example.ems;
import com.example.ems.config.TestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
@SpringBootTest
@Import(TestConfig.class)
public class EmsApplicationTests {
	@Test
	public void contextLoads() {
	}
}
