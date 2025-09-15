package com.example.ems;
import com.example.ems.config.GithubProperties;
import com.example.ems.config.TestConfig;
import com.example.ems.service.impl.GithubActionServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoReactiveDataAutoConfiguration;
import org.springframework.boot.autoconfigure.web.reactive.function.client.WebClientAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.vault.config.VaultAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
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
@ActiveProfiles("test")
@EnableAutoConfiguration(excludeName = {
		"org.springframework.cloud.vault.config.VaultAutoConfiguration"
})
class EmsApplicationTests {

	@MockBean
	private GithubProperties githubProperties;

	@MockBean
	private GithubActionServiceImpl githubActionService;

	@MockBean
	private ReactiveMongoTemplate mongoTemplate;  // <- Mocked

	@Test
	void contextLoads() {}
}
