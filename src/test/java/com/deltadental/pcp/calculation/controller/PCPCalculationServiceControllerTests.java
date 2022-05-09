package com.deltadental.pcp.calculation.controller;

import com.deltadental.pcp.calculation.domain.MemberContractClaimRequest;
import com.deltadental.pcp.calculation.service.MemberContractClaimService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@RunWith(MockitoJUnitRunner.class)
public class PCPCalculationServiceControllerTests {
    
    @InjectMocks
    PCPCalculationServiceController mockController;

    @Mock
    MemberContractClaimService mockMemberContractClaimService;

    @BeforeEach
    public void init(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testStageMembersContractsAndClaims_Success(){

        List<MemberContractClaimRequest> memberContractClaimRequests = buildRequest();
        Mockito.doNothing().when(mockMemberContractClaimService)
                .stageMemberContractClaimRecords(memberContractClaimRequests);
        ResponseEntity<Boolean> expectedResponse = mockController.stageMembersContractsAndClaims(memberContractClaimRequests);
        Assert.assertEquals(HttpStatus.CREATED, expectedResponse.getStatusCode());
        Assert.assertEquals(Boolean.TRUE, expectedResponse.getBody().booleanValue());

    }

    @Test
    public void testStageMembersContractsAndClaims_BadRequest(){

        List<MemberContractClaimRequest> memberContractClaimRequests = new ArrayList<>();
        Mockito.doNothing().when(mockMemberContractClaimService)
                .stageMemberContractClaimRecords(memberContractClaimRequests);
        ResponseEntity<Boolean> expectedResponse = mockController.stageMembersContractsAndClaims(memberContractClaimRequests);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, expectedResponse.getStatusCode());
        Assert.assertEquals(Boolean.FALSE, expectedResponse.getBody().booleanValue());

    }

    private List<MemberContractClaimRequest> buildRequest() {
        List<MemberContractClaimRequest> memberContractClaimRequests = new ArrayList<>();
        MemberContractClaimRequest request = new MemberContractClaimRequest();
        request.setClaimId("CL001");
        request.setMemberId("MEM001");
        request.setContractId("CONT001");
        request.setOperatorId("OP001");
        request.setState("CA");
        request.setProviderId("PR001");

        memberContractClaimRequests.add(request);
        return memberContractClaimRequests;
    }


}
