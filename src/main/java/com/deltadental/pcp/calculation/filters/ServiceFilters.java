package com.deltadental.pcp.calculation.filters;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.deltadental.platform.common.logger.LoggerFilter;

@Configuration
public class ServiceFilters {

	@Bean
	public FilterRegistrationBean loggerFilterRegistrationBean() {
		FilterRegistrationBean registrationBean = new FilterRegistrationBean();
		registrationBean.setName("Logger");
		registrationBean.setFilter(new LoggerFilter());
		registrationBean.setOrder(4);
		return registrationBean;
	}
}