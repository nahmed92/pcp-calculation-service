package com.deltadental.pcp.calculation.filters;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.web.servlet.FilterRegistrationBean;

import static org.junit.jupiter.api.Assertions.assertNotNull;


public class ServiceFiltersTests {

    @InjectMocks
    ServiceFilters mockServiceFilters;

    @BeforeEach
    public void init(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testLoggerFilterRegistrationBean(){
        FilterRegistrationBean expectedBean = mockServiceFilters.loggerFilterRegistrationBean();
        assertNotNull(expectedBean);
    }
}
