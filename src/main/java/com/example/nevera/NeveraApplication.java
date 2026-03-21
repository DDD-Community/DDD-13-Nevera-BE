package com.example.nevera;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class NeveraApplication {

	public static void main(String[] args) {
		SpringApplication.run(NeveraApplication.class, args);
	}

}
