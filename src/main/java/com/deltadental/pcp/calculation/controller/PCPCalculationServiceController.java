package com.deltadental.pcp.calculation.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.deltadental.platform.common.annotation.aop.MethodExecutionTime;

import io.swagger.annotations.Api;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(value = "/pcp-calculation")
@Api(value = "/pcp-calculation")
@Slf4j
@Data
public class PCPCalculationServiceController {

	
	@ResponseBody
	@MethodExecutionTime
    @GetMapping(value = "/hello", consumes = {MediaType.ALL_VALUE})
	public ResponseEntity<String> sayHello() {
		log.info("START PCPCalculationServiceController.sayHello");
		ResponseEntity<String> responseEntity = new ResponseEntity<>("Hello PCP Calculation Service!", HttpStatus.OK); 
		log.info("END PCPCalculationServiceController.sayHello");
		return responseEntity;
	}
}
