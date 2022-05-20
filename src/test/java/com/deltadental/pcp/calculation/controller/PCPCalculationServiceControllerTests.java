package com.deltadental.pcp.calculation.controller;

import com.deltadental.pcp.calculation.domain.MemberContractClaimRequest;
import com.deltadental.pcp.calculation.service.MemberContractClaimService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class PCPCalculationServiceControllerTests {
    
    @InjectMocks
    PCPCalculationServiceController mockController;

    @Mock
    MemberContractClaimService mockMemberContractClaimService;

    @BeforeEach
    public void setup(){
    }

    @Test
    void testStageMembersContractsAndClaims_Success(){

        List<MemberContractClaimRequest> memberContractClaimRequests = buildRequest();
        Mockito.doNothing().when(mockMemberContractClaimService)
                .stageMemberContractClaimRecords(memberContractClaimRequests);
        ResponseEntity<Boolean> expectedResponse = mockController.stageMembersContractsAndClaims(memberContractClaimRequests);
        assertEquals(HttpStatus.CREATED, expectedResponse.getStatusCode());
        assertEquals(Boolean.TRUE, expectedResponse.getBody().booleanValue());

    }

    @Test
    void testStageMembersContractsAndClaims_BadRequest(){

        List<MemberContractClaimRequest> memberContractClaimRequests = new ArrayList<>();
        ResponseEntity<Boolean> expectedResponse = mockController.stageMembersContractsAndClaims(memberContractClaimRequests);
        assertEquals(HttpStatus.BAD_REQUEST, expectedResponse.getStatusCode());
        assertEquals(Boolean.FALSE, expectedResponse.getBody().booleanValue());

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
