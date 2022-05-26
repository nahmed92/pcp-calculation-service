package com.deltadental.pcp.calculation.controller;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import com.deltadental.pcp.calculation.domain.MemberContractClaimRequest;
import com.deltadental.pcp.calculation.service.ExcelService;
import com.deltadental.pcp.calculation.service.MemberContractClaimService;

@ExtendWith(MockitoExtension.class)
public class ExcelPCPDataUploadControllerTest {

    @InjectMocks
    ExcelPCPDataUploadController mockController;

    @Mock
    MemberContractClaimService memberContractClaimService;

    @Mock
    ExcelService excelService;

    @BeforeEach
    public void setup(){

    }

    @Test
    public void testUploadPCPMemberClaims_success() throws Exception{
        File f = new File("src/test/resources/data/ExcelServiceTestData_empty.xlsx");
        byte[] bytes = Files.readAllBytes(f.toPath());
        MockMultipartFile file = new MockMultipartFile("Test",bytes);
        List<MemberContractClaimRequest> memberContractClaimRequests = buildMemberContractClaimRequests();
        Mockito.when(excelService.hasExcelFormat(file)).thenReturn(true);
        Mockito.when(excelService
                .extractPCPMemberClaimsData(file)).thenReturn(memberContractClaimRequests);
        Mockito.doNothing().when(memberContractClaimService).stageMemberContractClaimRecords(memberContractClaimRequests);
        ResponseEntity<String> expectedResults = mockController.uploadPCPMemberClaims(file);
        Assertions.assertEquals(expectedResults.getStatusCode(), HttpStatus.CREATED);
    }

    @Test
    public void testUploadPCPMemberClaims_hasExcelFormatFalse() throws Exception{
        File f = new File("src/test/resources/data/ExcelServiceTestData_empty.xlsx");
        byte[] bytes = Files.readAllBytes(f.toPath());
        MockMultipartFile file = new MockMultipartFile("Test",bytes);
        List<MemberContractClaimRequest> memberContractClaimRequests = buildMemberContractClaimRequests();
        Mockito.when(excelService.hasExcelFormat(file)).thenReturn(false);
        ResponseEntity<String> expectedResults = mockController.uploadPCPMemberClaims(file);
        Assertions.assertEquals(expectedResults.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    public void testUploadPCPMemberClaims_RequestIsNull() throws Exception{
        File f = new File("src/test/resources/data/ExcelServiceTestData_empty.xlsx");
        byte[] bytes = Files.readAllBytes(f.toPath());
        MockMultipartFile file = new MockMultipartFile("Test",bytes);
        Mockito.when(excelService.hasExcelFormat(file)).thenReturn(true);
        Mockito.when(excelService
                .extractPCPMemberClaimsData(file)).thenReturn(null);
        ResponseEntity<String> expectedResults = mockController.uploadPCPMemberClaims(file);
        Assertions.assertEquals(expectedResults.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    private List<MemberContractClaimRequest> buildMemberContractClaimRequests() {
        List<MemberContractClaimRequest> memberContractClaimRequests =
                new ArrayList<>();
        MemberContractClaimRequest request = MemberContractClaimRequest.builder()
                .claimId("CL001")
                .contractId("CR001")
                .memberId("M001")
                .operatorId("OPR001")
                .providerId("PR001")
                .state("CA")
                .build();
        memberContractClaimRequests.add(request);
        return memberContractClaimRequests;

    }

}
