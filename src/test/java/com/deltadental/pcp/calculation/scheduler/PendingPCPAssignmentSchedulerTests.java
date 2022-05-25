package com.deltadental.pcp.calculation.scheduler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.deltadental.pcp.calculation.worker.PCPCalculationServiceWorker;


public class PendingPCPAssignmentSchedulerTests {

    @InjectMocks
    PendingPCPAssignmentScheduler mockPendingPCPAssignmentScheduler;

    @Mock
    PCPCalculationServiceWorker mockWorker;

    @BeforeEach
    public void init(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testProcess(){
        Mockito.doNothing().when(mockWorker).processPCPAssignmentRequests();
        mockPendingPCPAssignmentScheduler.process();
    }
}
