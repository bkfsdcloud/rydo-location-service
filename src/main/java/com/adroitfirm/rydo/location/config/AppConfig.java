package com.adroitfirm.rydo.location.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class AppConfig {

	@Bean
	ObjectMapper objectMapper() {
		ObjectMapper restTemplate = new ObjectMapper();
		return restTemplate;
	}
}
