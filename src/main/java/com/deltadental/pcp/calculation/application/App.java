package com.deltadental.pcp.calculation.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@EnableScheduling
@EnableAsync
@Slf4j
@ComponentScan(basePackages = { "com.deltadental.*" })
public class App {

	public static void main(String[] args) {
		log.info("Starting PCP Calculation Service");
		SpringApplication.run(App.class, args);
	}

}
