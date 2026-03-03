package com.devgroup.enterprise_helpdesk_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class EnterpriseHelpdeskApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(EnterpriseHelpdeskApiApplication.class, args);
	}

}
