package com.example.ems;
import com.example.ems.config.TestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
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
@SpringBootTest(properties = {
		"github.token=dummy-token",
		"github.uri=https://api.github.com",
		"github.owner=poojasheela",
		"github.repo=ems",
		"github.workflowFileName=build.yml"
})
public class EmsApplicationTests {

	@Test
	void contextLoads() {
	}
}
