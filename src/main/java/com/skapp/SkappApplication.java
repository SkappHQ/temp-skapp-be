package com.skapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
@EntityScan(basePackages = { "com.skapp.community.peopleplanner.model", "com.skapp.community.common.model",
		"com.skapp.community.timeplanner.model", "com.skapp.community.leaveplanner.model" })
public class SkappApplication implements AsyncConfigurer {

	public static void main(String[] args) {
		SpringApplication.run(SkappApplication.class, args);
	}

}
