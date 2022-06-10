package com.deltadental.pcp.calculation.error;

import com.deltadental.platform.common.exception.ServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


public class PCPCalculationServiceErrorsTests {

    PCPCalculationServiceErrors mockPCPCalculationServiceErrors;

    @BeforeEach
    public void init() {

        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void test_PCPCalculationServiceErrors() {

        mockPCPCalculationServiceErrors = PCPCalculationServiceErrors.INTERNAL_SERVER_ERROR;
        ServiceException ex = mockPCPCalculationServiceErrors.createException(new Object());
        assertNotNull(ex);
        assertEquals(ex.getErrorCode(),
                PCPCalculationServiceErrors.INTERNAL_SERVER_ERROR.toString());

    }
}
