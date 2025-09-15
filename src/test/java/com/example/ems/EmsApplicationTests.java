package com.example.ems;
import com.example.ems.config.GithubProperties;
import com.example.ems.config.TestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.vault.config.VaultAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
//
//@SpringBootTest
//@ActiveProfiles("test")
//public class EmsApplicationTests {
//
//	@Test
//	public void contextLoads() {
//	}
//}

@SpringBootTest
@EnableAutoConfiguration(exclude = { VaultAutoConfiguration.class })
@ActiveProfiles("test")
class EmsApplicationTests {
	@MockBean
	private GithubProperties githubProperties;

	@Test
	void contextLoads() {}
}
