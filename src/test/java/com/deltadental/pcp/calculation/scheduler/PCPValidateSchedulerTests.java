package com.deltadental.pcp.calculation.scheduler;

import com.deltadental.pcp.calculation.service.PCPValidatorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class PCPValidateSchedulerTests {

    @InjectMocks
    PCPValidateScheduler mockPCPValidateScheduler;

    @Mock
    PCPValidatorService mockPCPValidatorService;

    @BeforeEach
    public void init(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testProcess(){
        Mockito.doNothing().when(mockPCPValidatorService).validatePending();
        mockPCPValidateScheduler.process();
    }
}
