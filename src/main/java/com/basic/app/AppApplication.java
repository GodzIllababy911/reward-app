package com.basic.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AppApplication {

	private static final Logger log = LoggerFactory.getLogger(AppApplication.class);

	public static void main(String[] args) {
	log.info("Starting Rewards Application...");
		SpringApplication.run(AppApplication.class, args);
		log.info("Rewards Application started successfully");
	}

}
