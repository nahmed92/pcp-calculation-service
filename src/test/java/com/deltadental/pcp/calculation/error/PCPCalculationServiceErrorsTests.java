package com.deltadental.pcp.calculation.error;

import com.deltadental.platform.common.exception.ServiceException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.Matchers.is;

@RunWith(MockitoJUnitRunner.class)
public class PCPCalculationServiceErrorsTests {

    PCPCalculationServiceErrors mockPCPCalculationServiceErrors;

    @BeforeEach
    public void init(){

        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void test_PCPCalculationServiceErrors(){

        mockPCPCalculationServiceErrors = PCPCalculationServiceErrors.INTERNAL_SERVER_ERROR;
        ServiceException ex = mockPCPCalculationServiceErrors.createException(new Object());
        Assert.assertNotNull(ex);
        Assert.assertThat(ex.getErrorCode(),
                is(PCPCalculationServiceErrors.INTERNAL_SERVER_ERROR.toString()));

    }
}
