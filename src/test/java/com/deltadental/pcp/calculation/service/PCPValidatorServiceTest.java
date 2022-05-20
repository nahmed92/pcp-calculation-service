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
    public void setup(){
        //MockitoAnnotations.initMocks(this);
        //ReflectionTestUtils.setField(PCPValidatorService.class, serviceInstanceId, "instance-id");
    }

    @Test
    public void testValidatePending_success(){

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
        List<ServiceLine> spyServiceLineList = Mockito.spy(memberClaimResponse.getServiceLines());
        Mockito.when(mockPCPConfigData.isExplanationCodeValid(spyServiceLineList))
                .thenReturn(true);
        Mockito.when(mockPCPConfigData.isProcedureCodeValid(spyServiceLineList))
                .thenReturn(true);
        Mockito.doNothing().when(mockPCPAssignmentService).process(contractEntity, memberClaimResponse);
        //List<ContractMemberClaimEntity> expectedRecords = mockContractMemberClaimRepo.findByInstanceIdWhereStatusInList(serviceInstanceId, SEARCH_STATUS_VALIDATE);
        mockPCPValidatorService.validatePending();
        assertEquals(1, spyList.size());

    }

    @Test
    public void testValidatePending_failure(){

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
