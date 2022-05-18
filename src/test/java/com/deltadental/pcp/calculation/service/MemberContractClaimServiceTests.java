package com.deltadental.pcp.calculation.service;

import com.deltadental.pcp.calculation.mapper.Mapper;
import com.deltadental.pcp.calculation.repos.ContractMemberClaimRepo;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class MemberContractClaimServiceTests {

    @InjectMocks
    MemberContractClaimService mockMemberContractClaimService;

    @Mock
    ContractMemberClaimRepo mockRepo;

    @Mock
    Mapper mapper;

    String serviceInstanceId = "instanceId";

    @BeforeEach
    public void init(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testSave_success(){

    }


}
