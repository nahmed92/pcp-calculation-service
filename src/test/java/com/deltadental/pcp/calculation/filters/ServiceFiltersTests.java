package com.deltadental.pcp.calculation.filters;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.web.servlet.FilterRegistrationBean;

@RunWith(MockitoJUnitRunner.class)
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
        Assert.assertNotNull(expectedBean);
    }
}
