package com.example.nevera;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class NeveraApplicationTests {

	@Value("${google.cloud.vision.credentials.location}")
	private String location;

	@Test
	void check_env_load() {
		System.out.println("로드된 경로: " + location);
		assertThat(location).isEqualTo("classpath:google-vision-key.json");
	}

	@Test
	void contextLoads() {
	}

}
