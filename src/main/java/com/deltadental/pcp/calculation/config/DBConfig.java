package com.deltadental.pcp.calculation.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = { "com.deltadental.pcp.calculation.repos" })
@EntityScan(basePackages = { "com.deltadental.pcp.calculation.entities" })
public class DBConfig {

	@Value("${spring.datasource.url}")
	String databaseUrl;

	@Value("${spring.datasource.username}")
	String databaseUser;

	@Value("${spring.datasource.password}")
	String databasePassword;

	@Value("${spring.datasource.driver-class}")
	String databaseDriver;

	// @Bean
	public DataSource dataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName(databaseDriver);
		dataSource.setUrl(databaseUrl);
		dataSource.setUsername(databaseUser);
		dataSource.setPassword(databasePassword);
		return dataSource;
	}
}
