package com.deltadental.pcp.calculation.service;

import com.deltadental.mtv.sync.interservice.MTVSyncServiceClient;
import com.deltadental.mtv.sync.interservice.dto.MemberClaimResponse;
import com.deltadental.mtv.sync.interservice.dto.ProviderAssignmentResponse;
import com.deltadental.mtv.sync.interservice.dto.ServiceLine;
import com.deltadental.pcp.calculation.entities.ContractMemberClaimEntity;
import com.deltadental.pcp.calculation.entities.ContractMemberClaimPK;
import com.deltadental.pcp.calculation.entities.MemberClaimEntity;
import com.deltadental.pcp.calculation.entities.MemberClaimServicesEntity;
import com.deltadental.pcp.calculation.entities.MemberProviderEntity;
import com.deltadental.pcp.calculation.enums.Status;
import com.deltadental.pcp.calculation.interservice.PCPConfigData;
import com.deltadental.pcp.calculation.repos.MemberClaimRepo;
import com.deltadental.pcp.calculation.repos.MemberClaimServicesRepo;
import com.deltadental.pcp.calculation.repos.MemberProviderRepo;
import com.deltadental.pcp.search.interservice.PCPSearchServiceClient;
import com.deltadental.pcp.search.interservice.PCPValidateResponse;
import com.deltadental.pcp.search.interservice.pojo.EnrolleeDetail;
import com.deltadental.pcp.search.interservice.pojo.PCPResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class PCPAssignmentServiceTest {

    @InjectMocks
    PCPAssignmentService pcpAssignmentService;

    @Mock
    MTVSyncServiceClient mockMtvSyncService;

    @Mock
    PCPConfigData mockPCPConfigData;

    @Mock
    PCPSearchServiceClient mockPCPSearchService;

    @Mock
    MemberProviderRepo mockMemberProviderRepo;

    @Mock
    MemberClaimServicesRepo mockMemberClaimServicesRepo;

    @Mock
    MemberClaimRepo mockMemberClaimRepo;

    @BeforeEach
    public void setup() {

    }

    @Test
    public void testProcess_success() {
        ContractMemberClaimEntity contractMemberClaimEntity = buildContractMemberClaimEntity();
        MemberClaimResponse memberClaimResponse = buildMemberClaimResponse();
        PCPValidateResponse pcpValidateResponse = buildPCPValidateResponse();
        ProviderAssignmentResponse providerAssignmentResponse = buildProviderAssignmentResponse();
        Mockito.when(mockPCPConfigData.calculatePCPEffectiveDate()).thenReturn("20022022");
        Mockito.when(mockPCPSearchService.pcpValidate(Mockito.any())).thenReturn(pcpValidateResponse);
        Mockito.when(mockMtvSyncService.providerAssignment(Mockito.any())).thenReturn(providerAssignmentResponse);
        Mockito.when(mockMemberClaimRepo.save(Mockito.any())).thenReturn(new MemberClaimEntity());
        Mockito.when(mockMemberClaimServicesRepo.save(Mockito.any())).thenReturn(new MemberClaimServicesEntity());
        Mockito.when(mockMemberProviderRepo.save(Mockito.any())).thenReturn(new MemberProviderEntity());
        pcpAssignmentService.process(contractMemberClaimEntity, memberClaimResponse);

    }

    @Test
    public void testProcess_throwException() {
        ContractMemberClaimEntity contractMemberClaimEntity = buildContractMemberClaimEntity();
        MemberClaimResponse memberClaimResponse = buildMemberClaimResponse();
        PCPValidateResponse pcpValidateResponse = buildPCPValidateResponse();
        Mockito.when(mockPCPConfigData.calculatePCPEffectiveDate()).thenReturn("20022022");
        Mockito.when(mockPCPSearchService.pcpValidate(Mockito.any())).thenReturn(pcpValidateResponse);
        Mockito.when(mockMtvSyncService.providerAssignment(Mockito.any())).thenReturn(null);
        pcpAssignmentService.process(contractMemberClaimEntity, memberClaimResponse);
        Assertions.assertEquals(contractMemberClaimEntity.getStatus(), Status.RETRY);
    }


    @Test
    public void testProcess_PCPValidationFailure() {
        ContractMemberClaimEntity contractMemberClaimEntity = buildContractMemberClaimEntity();
        MemberClaimResponse memberClaimResponse = buildMemberClaimResponse();
        PCPValidateResponse pcpValidateResponse = buildPCPValidateResponse();
        pcpValidateResponse.setProcessStatusCode("Failed");
        Mockito.when(mockPCPConfigData.calculatePCPEffectiveDate()).thenReturn("20022022");
        Mockito.when(mockPCPSearchService.pcpValidate(Mockito.any())).thenReturn(pcpValidateResponse);
        pcpAssignmentService.process(contractMemberClaimEntity, memberClaimResponse);
        Assertions.assertEquals(contractMemberClaimEntity.getStatus(), Status.FAILED);
    }

    @Test
    public void testProcess_PCPValidationEnrolleeError() {
        ContractMemberClaimEntity contractMemberClaimEntity = buildContractMemberClaimEntity();
        MemberClaimResponse memberClaimResponse = buildMemberClaimResponse();
        PCPValidateResponse pcpValidateResponse = buildPCPValidateResponse();
        List<String> errorMessage = new ArrayList<>();
        errorMessage.add("Input PCP is Not Valid for the Enrollee");
        pcpValidateResponse.getPcpResponses().get(0).getEnrollees().get(0).setErrorMessages(errorMessage);
        pcpValidateResponse.setProcessStatusCode("Failed");
        Mockito.when(mockPCPConfigData.calculatePCPEffectiveDate()).thenReturn("20022022");
        Mockito.when(mockPCPSearchService.pcpValidate(Mockito.any())).thenReturn(pcpValidateResponse);
        pcpAssignmentService.process(contractMemberClaimEntity, memberClaimResponse);
        Assertions.assertEquals(contractMemberClaimEntity.getStatus(), Status.FAILED);
    }

    @Test
    public void testProcess_PCPValidationResponseNull() {
        ContractMemberClaimEntity contractMemberClaimEntity = buildContractMemberClaimEntity();
        MemberClaimResponse memberClaimResponse = buildMemberClaimResponse();
        Mockito.when(mockPCPConfigData.calculatePCPEffectiveDate()).thenReturn("20022022");
        Mockito.when(mockPCPSearchService.pcpValidate(Mockito.any())).thenReturn(null);
        pcpAssignmentService.process(contractMemberClaimEntity, memberClaimResponse);
        Assertions.assertEquals(contractMemberClaimEntity.getStatus(), Status.RETRY);
    }

    @Test
    public void testProcess_PCPAssignmentNotOK() {
        ContractMemberClaimEntity contractMemberClaimEntity = buildContractMemberClaimEntity();
        MemberClaimResponse memberClaimResponse = buildMemberClaimResponse();
        PCPValidateResponse pcpValidateResponse = buildPCPValidateResponse();
        ProviderAssignmentResponse providerAssignmentResponse = buildProviderAssignmentResponse();
        providerAssignmentResponse.setReturnCode("NOTOK");
        Mockito.when(mockPCPConfigData.calculatePCPEffectiveDate()).thenReturn("20022022");
        Mockito.when(mockPCPSearchService.pcpValidate(Mockito.any())).thenReturn(pcpValidateResponse);
        Mockito.when(mockMtvSyncService.providerAssignment(Mockito.any())).thenReturn(providerAssignmentResponse);
        pcpAssignmentService.process(contractMemberClaimEntity, memberClaimResponse);
        Assertions.assertEquals(contractMemberClaimEntity.getStatus(), Status.FAILED);
    }

    private ProviderAssignmentResponse buildProviderAssignmentResponse() {
        return ProviderAssignmentResponse.builder()
                .returnCode("OK").build();
    }

    private PCPValidateResponse buildPCPValidateResponse() {
        PCPValidateResponse response = new PCPValidateResponse();
        List<PCPResponse> pcpResponses = new ArrayList<>();
        PCPResponse pcpResponse = new PCPResponse();
        pcpResponse.setContractId("CR001");
        List<EnrolleeDetail> enrolleeDetails = new ArrayList<>();
        EnrolleeDetail enrolleeDetail = new EnrolleeDetail();
        enrolleeDetail.setDivisionNumber("D001");
        enrolleeDetail.setGroupNumber("GR001");
        List<String> errormessages = new ArrayList<>();
        errormessages.add("Input PCP is Valid for the Enrollee");
        enrolleeDetail.setErrorMessages(errormessages);
        enrolleeDetails.add(enrolleeDetail);
        pcpResponse.setEnrollees(enrolleeDetails);
        pcpResponses.add(pcpResponse);
        response.setProcessStatusCode("Success");
        response.setPcpResponses(pcpResponses);
        return response;
    }

    private ContractMemberClaimEntity buildContractMemberClaimEntity() {
        ContractMemberClaimEntity entity = new ContractMemberClaimEntity();
        ContractMemberClaimPK id= new ContractMemberClaimPK();
        id.setId("ID0011");
        id.setSequenceId(2);
        entity.setContractMemberClaimPK(id);
        entity.setContractId("C001");
        entity.setClaimId("CL001");
        entity.setProviderId("PR001");
        entity.setOperatorId("OPR001");
        entity.setState("CA");
        return entity;
    }

    private MemberClaimResponse buildMemberClaimResponse() {
        MemberClaimResponse response = new MemberClaimResponse();
        response.setProviderId("PR001");
        response.setClaimId("CL001");
        response.setGroupNumber("GR001");
        response.setDivisionNumber("DN001");
        response.setClaimStatus("status");
        List<ServiceLine> serviceLines = new ArrayList<>();
        ServiceLine serviceLine = new ServiceLine();
        serviceLine.setSequenceNumber("1");
        serviceLine.setServiceNumber("S001");
        serviceLine.setServicePaidTs(new Timestamp((new Date()).getTime()));
        serviceLine.setServiceResolutionTs(new Timestamp((new Date()).getTime()));
        serviceLines.add(serviceLine);
        response.setPaidTs(new Timestamp((new Date()).getTime()));
        response.setReceivedTs(new Timestamp((new Date()).getTime()));
        response.setResolvedTs(new Timestamp((new Date()).getTime()));
        response.setServiceLines(serviceLines);
        return response;
    }
}
