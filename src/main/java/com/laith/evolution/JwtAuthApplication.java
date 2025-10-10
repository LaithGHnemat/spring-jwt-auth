package com.laith.evolution;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@SpringBootApplication
public class JwtAuthApplication {
	public static void main(String[] args) {
		SpringApplication.run(JwtAuthApplication.class, args);
	}
}
