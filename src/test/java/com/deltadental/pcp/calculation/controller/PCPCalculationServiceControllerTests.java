package com.deltadental.pcp.calculation.controller;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.deltadental.pcp.calculation.domain.MemberContractClaimRequest;
import com.deltadental.pcp.calculation.service.MemberContractClaimService;

//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestInstance(Lifecycle.PER_CLASS)
@RunWith(MockitoJUnitRunner.class)
public class PCPCalculationServiceControllerTests {
    
    @InjectMocks
    PCPCalculationServiceController mockController;

    @Mock
    MemberContractClaimService mockMemberContractClaimService;

    @BeforeAll
    public void initialize(){

        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testStageMembersContractsAndClaims_Success(){

        List<MemberContractClaimRequest> memberContractClaimRequests = buildRequest();
        Mockito.doNothing().when(mockMemberContractClaimService)
                .stageMemberContractClaimRecords(memberContractClaimRequests);
        ResponseEntity<Boolean> expectedResponse = mockController.stageMembersContractsAndClaims(memberContractClaimRequests);
        Assert.assertEquals(HttpStatus.CREATED, expectedResponse.getStatusCode());
        Assert.assertEquals(Boolean.TRUE, expectedResponse.getBody().booleanValue());

    }

    @Test
    void testStageMembersContractsAndClaims_BadRequest(){

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
