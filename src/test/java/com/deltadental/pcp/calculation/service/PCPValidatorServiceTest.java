package com.deltadental.pcp.calculation.service;

import com.deltadental.mtv.sync.interservice.MTVSyncServiceClient;
import com.deltadental.mtv.sync.interservice.dto.MemberClaimResponse;
import com.deltadental.mtv.sync.interservice.dto.ServiceLine;
import com.deltadental.pcp.calculation.entities.ContractMemberClaimEntity;
import com.deltadental.pcp.calculation.enums.Status;
import com.deltadental.pcp.calculation.interservice.PCPConfigData;
import com.deltadental.pcp.calculation.repos.ContractMemberClaimRepo;
import org.apache.commons.lang.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClientException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class PCPValidatorServiceTest {

    @InjectMocks
    PCPValidatorService mockPCPValidatorService;

    @Mock
    MTVSyncServiceClient mockMTVSyncServiceClient;

    @Mock
    PCPConfigData mockPCPConfigData;

    @Mock
    ContractMemberClaimRepo mockContractMemberClaimRepo;

    @Mock
    PCPAssignmentService mockPCPAssignmentService;

    String serviceInstanceId;

    private static final List<Status> SEARCH_STATUS_VALIDATE = List.of(Status.RETRY, Status.STAGED);

    @BeforeEach
    public void setup() {
    }

    @Test
    public void testValidatePending_success() {

        ContractMemberClaimEntity contractEntity = buildContractMemberClaimEntity();
        List<ContractMemberClaimEntity> spyList = Mockito.spy(ArrayList.class);
        spyList.add(contractEntity);
        MemberClaimResponse memberClaimResponse = buildMemberClaimResponse();
        Mockito.when(
                        mockContractMemberClaimRepo.findByInstanceIdWhereStatusInList(serviceInstanceId, SEARCH_STATUS_VALIDATE))
                .thenReturn(spyList);
        Mockito.when(mockMTVSyncServiceClient.memberClaim(contractEntity.getClaimId())).thenReturn(memberClaimResponse);

        Mockito.when(mockPCPConfigData.isProviderInExclusionList(memberClaimResponse.getProviderId(), memberClaimResponse.getGroupNumber(), memberClaimResponse.getDivisionNumber()))
                .thenReturn(true);
        Mockito.when(mockPCPConfigData.isProviderInInclusionList(memberClaimResponse.getProviderId(), memberClaimResponse.getGroupNumber(), memberClaimResponse.getDivisionNumber()))
                .thenReturn(true);
        Mockito.when(mockPCPConfigData.isClaimStatusValid(StringUtils.trimToNull(memberClaimResponse.getClaimStatus())))
                .thenReturn(true);
        Mockito.when(mockPCPConfigData.isExplanationCodeValid(ArgumentMatchers.any()))
                .thenReturn(true);
        Mockito.when(mockPCPConfigData.isProcedureCodeValid(ArgumentMatchers.any()))
                .thenReturn(true);
        Mockito.doNothing().when(mockPCPAssignmentService).process(contractEntity, memberClaimResponse);
        mockPCPValidatorService.validatePending();
        assertEquals(1, spyList.size());

    }

    @Test
    public void testValidatePending_inclusionIsFalse() {

        ContractMemberClaimEntity contractEntity = buildContractMemberClaimEntity();
        List<ContractMemberClaimEntity> spyList = Mockito.spy(ArrayList.class);
        spyList.add(contractEntity);
        MemberClaimResponse memberClaimResponse = buildMemberClaimResponse();
        Mockito.when(
                        mockContractMemberClaimRepo.findByInstanceIdWhereStatusInList(serviceInstanceId, SEARCH_STATUS_VALIDATE))
                .thenReturn(spyList);
        Mockito.when(mockMTVSyncServiceClient.memberClaim(contractEntity.getClaimId())).thenReturn(memberClaimResponse);

        Mockito.when(mockPCPConfigData.isProviderInExclusionList(memberClaimResponse.getProviderId(), memberClaimResponse.getGroupNumber(), memberClaimResponse.getDivisionNumber()))
                .thenReturn(true);
        Mockito.when(mockPCPConfigData.isProviderInInclusionList(memberClaimResponse.getProviderId(), memberClaimResponse.getGroupNumber(), memberClaimResponse.getDivisionNumber()))
                .thenReturn(false);
        Mockito.when(mockPCPConfigData.isClaimStatusValid(StringUtils.trimToNull(memberClaimResponse.getClaimStatus())))
                .thenReturn(true);
        Mockito.when(mockPCPConfigData.isExplanationCodeValid(ArgumentMatchers.any()))
                .thenReturn(true);
        Mockito.when(mockPCPConfigData.isProcedureCodeValid(ArgumentMatchers.any()))
                .thenReturn(true);
        Mockito.doNothing().when(mockPCPAssignmentService).process(contractEntity, memberClaimResponse);
        mockPCPValidatorService.validatePending();
        assertEquals(1, spyList.size());

    }

    @Test
    public void testValidatePending_ExclusionIsFalse() {

        ContractMemberClaimEntity contractEntity = buildContractMemberClaimEntity();
        List<ContractMemberClaimEntity> spyList = Mockito.spy(ArrayList.class);
        spyList.add(contractEntity);
        MemberClaimResponse memberClaimResponse = buildMemberClaimResponse();
        Mockito.when(
                        mockContractMemberClaimRepo.findByInstanceIdWhereStatusInList(serviceInstanceId, SEARCH_STATUS_VALIDATE))
                .thenReturn(spyList);
        Mockito.when(mockMTVSyncServiceClient.memberClaim(contractEntity.getClaimId())).thenReturn(memberClaimResponse);

        Mockito.when(mockPCPConfigData.isProviderInExclusionList(memberClaimResponse.getProviderId(), memberClaimResponse.getGroupNumber(), memberClaimResponse.getDivisionNumber()))
                .thenReturn(false);
        Mockito.when(mockPCPConfigData.isProviderInInclusionList(memberClaimResponse.getProviderId(), memberClaimResponse.getGroupNumber(), memberClaimResponse.getDivisionNumber()))
                .thenReturn(true);
        Mockito.when(mockPCPConfigData.isClaimStatusValid(StringUtils.trimToNull(memberClaimResponse.getClaimStatus())))
                .thenReturn(true);
        Mockito.when(mockPCPConfigData.isExplanationCodeValid(ArgumentMatchers.any()))
                .thenReturn(true);
        Mockito.when(mockPCPConfigData.isProcedureCodeValid(ArgumentMatchers.any()))
                .thenReturn(true);
        mockPCPValidatorService.validatePending();
        assertEquals(1, spyList.size());

    }

    @Test
    public void testValidatePending_claimStatusIsFalse() {

        ContractMemberClaimEntity contractEntity = buildContractMemberClaimEntity();
        List<ContractMemberClaimEntity> spyList = Mockito.spy(ArrayList.class);
        spyList.add(contractEntity);
        MemberClaimResponse memberClaimResponse = buildMemberClaimResponse();
        Mockito.when(
                        mockContractMemberClaimRepo.findByInstanceIdWhereStatusInList(serviceInstanceId, SEARCH_STATUS_VALIDATE))
                .thenReturn(spyList);
        Mockito.when(mockMTVSyncServiceClient.memberClaim(contractEntity.getClaimId())).thenReturn(memberClaimResponse);

        Mockito.when(mockPCPConfigData.isProviderInExclusionList(memberClaimResponse.getProviderId(), memberClaimResponse.getGroupNumber(), memberClaimResponse.getDivisionNumber()))
                .thenReturn(true);
        Mockito.when(mockPCPConfigData.isProviderInInclusionList(memberClaimResponse.getProviderId(), memberClaimResponse.getGroupNumber(), memberClaimResponse.getDivisionNumber()))
                .thenReturn(true);
        Mockito.when(mockPCPConfigData.isClaimStatusValid(StringUtils.trimToNull(memberClaimResponse.getClaimStatus())))
                .thenReturn(false);
        Mockito.when(mockPCPConfigData.isExplanationCodeValid(ArgumentMatchers.any()))
                .thenReturn(true);
        Mockito.when(mockPCPConfigData.isProcedureCodeValid(ArgumentMatchers.any()))
                .thenReturn(true);
        mockPCPValidatorService.validatePending();
        assertEquals(1, spyList.size());

    }

    @Test
    public void testValidatePending_ExplainationCodeIsFalse() {

        ContractMemberClaimEntity contractEntity = buildContractMemberClaimEntity();
        List<ContractMemberClaimEntity> spyList = Mockito.spy(ArrayList.class);
        spyList.add(contractEntity);
        MemberClaimResponse memberClaimResponse = buildMemberClaimResponse();
        Mockito.when(
                        mockContractMemberClaimRepo.findByInstanceIdWhereStatusInList(serviceInstanceId, SEARCH_STATUS_VALIDATE))
                .thenReturn(spyList);
        Mockito.when(mockMTVSyncServiceClient.memberClaim(contractEntity.getClaimId())).thenReturn(memberClaimResponse);

        Mockito.when(mockPCPConfigData.isProviderInExclusionList(memberClaimResponse.getProviderId(), memberClaimResponse.getGroupNumber(), memberClaimResponse.getDivisionNumber()))
                .thenReturn(true);
        Mockito.when(mockPCPConfigData.isProviderInInclusionList(memberClaimResponse.getProviderId(), memberClaimResponse.getGroupNumber(), memberClaimResponse.getDivisionNumber()))
                .thenReturn(true);
        Mockito.when(mockPCPConfigData.isClaimStatusValid(StringUtils.trimToNull(memberClaimResponse.getClaimStatus())))
                .thenReturn(true);
        Mockito.when(mockPCPConfigData.isExplanationCodeValid(ArgumentMatchers.any()))
                .thenReturn(false);
        Mockito.when(mockPCPConfigData.isProcedureCodeValid(ArgumentMatchers.any()))
                .thenReturn(true);
        mockPCPValidatorService.validatePending();
        assertEquals(1, spyList.size());

    }

    @Test
    public void testValidatePending_ProcedureCodeIsFalse() {

        ContractMemberClaimEntity contractEntity = buildContractMemberClaimEntity();
        List<ContractMemberClaimEntity> spyList = Mockito.spy(ArrayList.class);
        spyList.add(contractEntity);
        MemberClaimResponse memberClaimResponse = buildMemberClaimResponse();
        Mockito.when(
                        mockContractMemberClaimRepo.findByInstanceIdWhereStatusInList(serviceInstanceId, SEARCH_STATUS_VALIDATE))
                .thenReturn(spyList);
        Mockito.when(mockMTVSyncServiceClient.memberClaim(contractEntity.getClaimId())).thenReturn(memberClaimResponse);

        Mockito.when(mockPCPConfigData.isProviderInExclusionList(memberClaimResponse.getProviderId(), memberClaimResponse.getGroupNumber(), memberClaimResponse.getDivisionNumber()))
                .thenReturn(true);
        Mockito.when(mockPCPConfigData.isProviderInInclusionList(memberClaimResponse.getProviderId(), memberClaimResponse.getGroupNumber(), memberClaimResponse.getDivisionNumber()))
                .thenReturn(false);
        Mockito.when(mockPCPConfigData.isClaimStatusValid(StringUtils.trimToNull(memberClaimResponse.getClaimStatus())))
                .thenReturn(true);
        Mockito.when(mockPCPConfigData.isExplanationCodeValid(ArgumentMatchers.any()))
                .thenReturn(true);
        Mockito.when(mockPCPConfigData.isProcedureCodeValid(ArgumentMatchers.any()))
                .thenReturn(false);
        mockPCPValidatorService.validatePending();
        assertEquals(1, spyList.size());

    }

    @Test
    public void testValidatePending_memberClaimResponseIsNull() {

        ContractMemberClaimEntity contractEntity = buildContractMemberClaimEntity();
        List<ContractMemberClaimEntity> spyList = Mockito.spy(ArrayList.class);
        spyList.add(contractEntity);
        Mockito.when(
                        mockContractMemberClaimRepo.findByInstanceIdWhereStatusInList(serviceInstanceId, SEARCH_STATUS_VALIDATE))
                .thenReturn(spyList);
        Mockito.when(mockMTVSyncServiceClient.memberClaim(contractEntity.getClaimId())).thenReturn(null);

        mockPCPValidatorService.validatePending();
        assertEquals(1, spyList.size());

    }

    @Test
    public void testValidatePending_memberClaimResponseIsErrorCodeIsNull() {

        ContractMemberClaimEntity contractEntity = buildContractMemberClaimEntity();
        List<ContractMemberClaimEntity> spyList = Mockito.spy(ArrayList.class);
        spyList.add(contractEntity);
        MemberClaimResponse memberClaimResponse = buildMemberClaimResponse();
        memberClaimResponse.setErrorCode(null);
        Mockito.when(
                        mockContractMemberClaimRepo.findByInstanceIdWhereStatusInList(serviceInstanceId, SEARCH_STATUS_VALIDATE))
                .thenReturn(spyList);
        Mockito.when(mockMTVSyncServiceClient.memberClaim(contractEntity.getClaimId())).thenReturn(memberClaimResponse);
        mockPCPValidatorService.validatePending();
        assertEquals(1, spyList.size());

    }

    @Test
    public void testValidatePending_memberClaimResponseIsErrorMessageIsNull() {

        ContractMemberClaimEntity contractEntity = buildContractMemberClaimEntity();
        List<ContractMemberClaimEntity> spyList = Mockito.spy(ArrayList.class);
        spyList.add(contractEntity);
        MemberClaimResponse memberClaimResponse = buildMemberClaimResponse();
        memberClaimResponse.setErrorMessage(null);
        Mockito.when(
                        mockContractMemberClaimRepo.findByInstanceIdWhereStatusInList(serviceInstanceId, SEARCH_STATUS_VALIDATE))
                .thenReturn(spyList);
        Mockito.when(mockMTVSyncServiceClient.memberClaim(contractEntity.getClaimId())).thenReturn(memberClaimResponse);
        mockPCPValidatorService.validatePending();
        assertEquals(1, spyList.size());

    }

    @Test
    public void testValidatePending_whenInclusionAndExclusionIsFalse() {

        ContractMemberClaimEntity contractEntity = buildContractMemberClaimEntity();
        List<ContractMemberClaimEntity> spyList = Mockito.spy(ArrayList.class);
        spyList.add(contractEntity);
        MemberClaimResponse memberClaimResponse = buildMemberClaimResponse();
        Mockito.when(
                        mockContractMemberClaimRepo.findByInstanceIdWhereStatusInList(serviceInstanceId, SEARCH_STATUS_VALIDATE))
                .thenReturn(spyList);
        Mockito.when(mockMTVSyncServiceClient.memberClaim(contractEntity.getClaimId())).thenReturn(memberClaimResponse);

        Mockito.when(mockPCPConfigData.isProviderInExclusionList(memberClaimResponse.getProviderId(), memberClaimResponse.getGroupNumber(), memberClaimResponse.getDivisionNumber()))
                .thenReturn(false);
        Mockito.when(mockPCPConfigData.isProviderInInclusionList(memberClaimResponse.getProviderId(), memberClaimResponse.getGroupNumber(), memberClaimResponse.getDivisionNumber()))
                .thenReturn(false);
        mockPCPValidatorService.validatePending();
        assertEquals(1, spyList.size());

    }

    @Test
    public void testValidatePending_whenServiceLineIsEmpty() {

        ContractMemberClaimEntity contractEntity = buildContractMemberClaimEntity();
        List<ContractMemberClaimEntity> spyList = Mockito.spy(ArrayList.class);
        spyList.add(contractEntity);
        MemberClaimResponse memberClaimResponse = buildMemberClaimResponse();
        Mockito.when(
                        mockContractMemberClaimRepo.findByInstanceIdWhereStatusInList(serviceInstanceId, SEARCH_STATUS_VALIDATE))
                .thenReturn(spyList);
        Mockito.when(mockMTVSyncServiceClient.memberClaim(contractEntity.getClaimId())).thenReturn(memberClaimResponse);

        Mockito.when(mockPCPConfigData.isProviderInExclusionList(memberClaimResponse.getProviderId(), memberClaimResponse.getGroupNumber(), memberClaimResponse.getDivisionNumber()))
                .thenReturn(true);
        Mockito.when(mockPCPConfigData.isProviderInInclusionList(memberClaimResponse.getProviderId(), memberClaimResponse.getGroupNumber(), memberClaimResponse.getDivisionNumber()))
                .thenReturn(true);
        memberClaimResponse.setServiceLines(null);
        mockPCPValidatorService.validatePending();
        assertEquals(1, spyList.size());

    }

    @Test
    public void testValidatePending_failure() {

        String expectedErrorMessage = "Exception occured during retriving member claim information from Metavance Sync Service. Test Exception";
        ContractMemberClaimEntity contractEntity = buildContractMemberClaimEntity();
        List<ContractMemberClaimEntity> spyList = Mockito.spy(ArrayList.class);
        spyList.add(contractEntity);
        MemberClaimResponse memberClaimResponse = buildMemberClaimResponse();
        Mockito.when(
                        mockContractMemberClaimRepo.findByInstanceIdWhereStatusInList(serviceInstanceId, SEARCH_STATUS_VALIDATE))
                .thenReturn(spyList);
        Mockito.when(mockMTVSyncServiceClient.memberClaim(contractEntity.getClaimId())).thenThrow(new RestClientException("Test Exception"));
        mockPCPValidatorService.validatePending();
        assertEquals(Status.RETRY, contractEntity.getStatus());
        assertEquals(expectedErrorMessage, contractEntity.getErrorMessage());

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
        serviceLines.add(serviceLine);
        response.setServiceLines(serviceLines);
        return response;
    }

    private ContractMemberClaimEntity buildContractMemberClaimEntity() {
        ContractMemberClaimEntity entity = new ContractMemberClaimEntity();
        entity.setId("ID0011");
        entity.setContractId("C001");
        entity.setClaimId("CL001");
        entity.setProviderId("PR001");
        entity.setOperatorId("OPR001");
        entity.setState("CA");
        return entity;
    }

}
