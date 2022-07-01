package com.deltadental.pcp.calculation.filters;

import com.deltadental.platform.common.logger.LoggerFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceFilters {

    @Bean
    public FilterRegistrationBean<LoggerFilter> loggerFilterRegistrationBean() {
        FilterRegistrationBean<LoggerFilter> registrationBean = new FilterRegistrationBean<LoggerFilter>();
        registrationBean.setName("Logger");
        registrationBean.setFilter(new LoggerFilter());
        registrationBean.setOrder(1);
        return registrationBean;
    }
}