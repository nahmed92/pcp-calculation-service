package com.deltadental.pcp.calculation.config;


import java.util.concurrent.Executor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = {"com.deltadental.pcp.calculation.repos"})
@EntityScan(basePackages = {"com.deltadental.pcp.calculation.entities"})
public class ApplicationConfig implements AsyncConfigurer {

	@Value("${pcp.calculation.workers:2}")
	private Integer maxPoolSize;
    @Override
    @Bean
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setMaxPoolSize(maxPoolSize);
        executor.setThreadNamePrefix("PCP-Calculation-");
        executor.initialize();
        return executor;
    }
}
