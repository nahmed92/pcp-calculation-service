package com.deltadental.pcp.calculation.config;

import java.util.concurrent.Executor;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = {"com.deltadental.pcp.calculation.repos"})
@EntityScan(basePackages = {"com.deltadental.pcp.calculation.entities"})
@ComponentScan(basePackages = {"com.deltadental.pcp.config","com.deltadental.pcp.security"})
public class ApplicationConfig implements AsyncConfigurer {
 
	
    @Override
    @Bean
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5); // FIXME: properties file
        executor.setMaxPoolSize(20);
        executor.setQueueCapacity(10);
        executor.setThreadNamePrefix("PCP-Calculation-");
        executor.initialize();
        return executor;
    }
}
