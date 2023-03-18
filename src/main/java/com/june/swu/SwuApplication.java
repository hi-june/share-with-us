package com.june.swu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class SwuApplication {

	public static void main(String[] args) {
		SpringApplication.run(SwuApplication.class, args);
	}

}
