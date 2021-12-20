package com.deltadental.pcp.calculation.application;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@ComponentScan(basePackages = { "com.deltadental.pcp.calculation.*" })
@EnableSwagger2
public class PCPCalculationServiceApplication {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
