package com.gcp.practise.parking.handler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class HandlerApplication {
	public static void main(String[] args) {
		SpringApplication.run(HandlerApplication.class, args);
	}
}
