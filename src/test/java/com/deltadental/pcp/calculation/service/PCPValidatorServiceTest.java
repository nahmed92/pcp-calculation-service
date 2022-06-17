package com.deltadental.pcp.calculation.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

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

import com.deltadental.mtv.sync.interservice.MTVSyncServiceClient;
import com.deltadental.mtv.sync.interservice.dto.MemberClaimResponse;
import com.deltadental.mtv.sync.interservice.dto.ServiceLine;
import com.deltadental.pcp.calculation.entities.ContractMemberClaimEntity;
import com.deltadental.pcp.calculation.entities.ContractMemberClaimPK;
import com.deltadental.pcp.calculation.enums.Status;
import com.deltadental.pcp.calculation.interservice.PCPConfigData;
import com.deltadental.pcp.calculation.repos.ContractMemberClaimRepo;
import com.deltadental.pcp.calculation.util.MemberClaimUtils;

@ExtendWith(MockitoExtension.class)
public class PCPValidatorServiceTest {

    @InjectMocks
    PCPValidatorService mockPCPValidatorService;
    
    @Mock
    MTVSyncServiceClient mockMTVSyncServiceClient;

    @Mock
    PCPConfigData mockPCPConfigData;
    
    @Mock
    MemberClaimUtils memberClaimUtills;

    @Mock
    ContractMemberClaimRepo mockContractMemberClaimRepo;

    @Mock
    PCPAssignmentService mockPCPAssignmentService;

    String serviceInstanceId;

    private static final List<Status> SEARCH_STATUS_VALIDATE = List.of(Status.RETRY, Status.STAGED, Status.VALIDATED, Status.PCP_EXCLUDED, Status.PCP_NOT_INCLUDED);

    @BeforeEach
    public void setup() {
    }

    @Test
    public void testValidatePending_success() {

        ContractMemberClaimEntity contractEntity = buildContractMemberClaimEntity();
        List<String> claimIds = List.of(contractEntity.getClaimId());
        List<ContractMemberClaimEntity> spyList = Mockito.spy(ArrayList.class);
        spyList.add(contractEntity);
        MemberClaimResponse memberClaimResponse = buildMemberClaimResponse();
        Mockito.when(
                        mockContractMemberClaimRepo.findByInstanceIdWhereStatusInList(serviceInstanceId, SEARCH_STATUS_VALIDATE))
                .thenReturn(spyList);
        Mockito.when(mockMTVSyncServiceClient.memberClaim(claimIds)).thenReturn( List.of(memberClaimResponse));

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
        Mockito.when(mockMTVSyncServiceClient.memberClaim( List.of(contractEntity.getClaimId()))).thenReturn( List.of(memberClaimResponse));

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
        Mockito.when(mockMTVSyncServiceClient.memberClaim(List.of(contractEntity.getClaimId()))).thenReturn(List.of(memberClaimResponse));

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
        Mockito.when(mockMTVSyncServiceClient.memberClaim(List.of(contractEntity.getClaimId()))).thenReturn(List.of(memberClaimResponse));

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
        Mockito.when(mockMTVSyncServiceClient.memberClaim(List.of(contractEntity.getClaimId()))).thenReturn(List.of(memberClaimResponse));

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
        Mockito.when(mockMTVSyncServiceClient.memberClaim(List.of(contractEntity.getClaimId()))).thenReturn( List.of(memberClaimResponse));

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
        Mockito.when(mockMTVSyncServiceClient.memberClaim( List.of(contractEntity.getClaimId()))).thenReturn(null);

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
        Mockito.when(mockMTVSyncServiceClient.memberClaim( List.of(contractEntity.getClaimId()))).thenReturn(( List.of(memberClaimResponse)));
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
        Mockito.when(mockMTVSyncServiceClient.memberClaim( List.of(contractEntity.getClaimId()))).thenReturn( List.of(memberClaimResponse));
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
        Mockito.when(mockMTVSyncServiceClient.memberClaim( List.of(contractEntity.getClaimId()))).thenReturn( List.of(memberClaimResponse));

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
        Mockito.when(mockMTVSyncServiceClient.memberClaim( List.of(contractEntity.getClaimId()))).thenReturn( List.of(memberClaimResponse));

        Mockito.when(mockPCPConfigData.isProviderInExclusionList(memberClaimResponse.getProviderId(), memberClaimResponse.getGroupNumber(), memberClaimResponse.getDivisionNumber()))
                .thenReturn(true);
        Mockito.when(mockPCPConfigData.isProviderInInclusionList(memberClaimResponse.getProviderId(), memberClaimResponse.getGroupNumber(), memberClaimResponse.getDivisionNumber()))
                .thenReturn(true);
        memberClaimResponse.setServiceLines(null);
        mockPCPValidatorService.validatePending();
        assertEquals(1, spyList.size());

    }

   // @Test
    public void testValidatePending_failure() {

        String expectedErrorMessage = "Exception occurred during retrieving member claim information from Metavance Sync Service. Test Exception";
        ContractMemberClaimEntity contractEntity = buildContractMemberClaimEntity();
        List<ContractMemberClaimEntity> spyList = Mockito.spy(ArrayList.class);
        spyList.add(contractEntity);
        MemberClaimResponse memberClaimResponse = buildMemberClaimResponse();
        Mockito.when(
                        mockContractMemberClaimRepo.findByInstanceIdWhereStatusInList(serviceInstanceId, SEARCH_STATUS_VALIDATE))
                .thenReturn(spyList);
        Mockito.when(mockMTVSyncServiceClient.memberClaim(List.of(contractEntity.getClaimId()))).thenThrow(new RestClientException("Test Exception"));
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
        ContractMemberClaimPK id = new ContractMemberClaimPK();
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

}
