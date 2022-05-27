package com.deltadental.pcp.calculation.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.assertj.core.util.Arrays;
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
    }

    @Test
    public void testValidatePending_success(){

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
    public void testValidatePending_inclusionIsFalse(){

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
    public void testValidatePending_ExclusionIsFalse(){

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
    public void testValidatePending_claimStatusIsFalse(){

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
    public void testValidatePending_ExplainationCodeIsFalse(){

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
    public void testValidatePending_ProcedureCodeIsFalse(){

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
                .thenReturn(false);
        mockPCPValidatorService.validatePending();
        assertEquals(1, spyList.size());

    }

    @Test
    public void testValidatePending_memberClaimResponseIsNull(){

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
    public void testValidatePending_memberClaimResponseIsErrorCodeIsNull(){

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
    public void testValidatePending_memberClaimResponseIsErrorMessageIsNull(){

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
    public void testValidatePending_whenInclusionAndExclusionIsFalse(){

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
    public void testValidatePending_whenServiceLineIsEmpty(){

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
        Mockito.when(mockMTVSyncServiceClient.memberClaim(List.of(contractEntity.getClaimId()))).thenThrow(new RestClientException("Test Exception"));
        mockPCPValidatorService.validatePending();
        assertEquals(Status.RETRY, contractEntity.getStatus());
        assertEquals(expectedErrorMessage, contractEntity.getErrorMessage());

    }
    
    @Test
    public void testCalculateLatestClaim() throws Exception {
    	DateFormat df = new SimpleDateFormat("MM-dd-yyyy", Locale.US);
    	List<ServiceLine> member01ServiceLine = List.of(getServiceLine(df.parse("03-04-2022"), df.parse("03-04-2022")),
    			                                        getServiceLine(df.parse("03-05-2022"), df.parse("03-05-2022")),
    	                                                getServiceLine(df.parse("04-05-2022"), df.parse("04-05-2022")));
    	
    	List<ServiceLine> member02ServiceLine = List.of(getServiceLine(df.parse("03-04-2021"), df.parse("03-04-2021")),
                                                        getServiceLine(df.parse("03-05-2021"), df.parse("03-05-2021")),
                                                        getServiceLine(df.parse("03-06-2022"), df.parse("03-06-2022")));
    	
    	MemberClaimResponse memberClaimResponse01 = MemberClaimResponse.builder()
    			.claimId("11113344")
    			.contractId("12345678")
    			.memberID("01")
    			.receivedTs(Timestamp.valueOf("2022-06-01 00:00:00.527"))
    			.serviceLines(member01ServiceLine)
    			.build();
    	
    	MemberClaimResponse memberClaimResponse02 = MemberClaimResponse.builder()
    			.claimId("22223344")
    			.contractId("12345678")
    			.memberID("02")
    			.serviceLines(member02ServiceLine)
    			.receivedTs(Timestamp.valueOf("2022-06-25 00:00:00.527"))
    			.build();
    	
    	MemberClaimResponse response = mockPCPValidatorService.calculateLatestClaim(List.of(memberClaimResponse01, memberClaimResponse02));
    	assertEquals(response.getClaimId(), "11113344");
    }
    
    @Test
    public void testCalculateLatestClaimThathasLatestThruAndOldFromDate() throws Exception {
    	DateFormat df = new SimpleDateFormat("MM-dd-yyyy", Locale.US);
    	List<ServiceLine> member01ServiceLine = List.of(getServiceLine(df.parse("03-04-2022"), df.parse("03-04-2022")),
    			                                        getServiceLine(df.parse("03-05-2022"), df.parse("03-05-2022")),
    	                                                getServiceLine(df.parse("04-05-2021"), df.parse("04-05-2022")));
    	
    	List<ServiceLine> member02ServiceLine = List.of(getServiceLine(df.parse("03-04-2021"), df.parse("03-04-2021")),
                                                        getServiceLine(df.parse("03-05-2021"), df.parse("03-05-2021")),
                                                        getServiceLine(df.parse("03-06-2022"), df.parse("03-06-2022")));
    	
    	MemberClaimResponse memberClaimResponse01 = MemberClaimResponse.builder()
    			.claimId("11113344")
    			.contractId("12345678")
    			.memberID("01")
    			.receivedTs(Timestamp.valueOf("2022-06-25 00:00:00.527"))
    			.serviceLines(member01ServiceLine)
    			.build();
    	
    	MemberClaimResponse memberClaimResponse02 = MemberClaimResponse.builder()
    			.claimId("22223344")
    			.contractId("12345678")
    			.memberID("02")
    			.serviceLines(member02ServiceLine)
    			.receivedTs(Timestamp.valueOf("2022-06-06 00:00:00.527"))
    			.build();
    	
    	MemberClaimResponse response = mockPCPValidatorService.calculateLatestClaim(List.of(memberClaimResponse01, memberClaimResponse02));
    	assertEquals(response.getClaimId(), "22223344");
    }
    
    @Test
    public void testCalculateLatestClaimWhenTwoMaxDateAreSame() throws Exception {
    	DateFormat df = new SimpleDateFormat("MM-dd-yyyy", Locale.US);
    	List<ServiceLine> member01ServiceLine = List.of(getServiceLine(df.parse("03-04-2022"), df.parse("03-04-2022")),
    			                                        getServiceLine(df.parse("03-05-2022"), df.parse("03-05-2022")),
    	                                                getServiceLine(df.parse("04-05-2022"), df.parse("04-05-2022")));
    	
    	List<ServiceLine> member02ServiceLine = List.of(getServiceLine(df.parse("03-04-2021"), df.parse("03-04-2021")),
                                                        getServiceLine(df.parse("03-05-2021"), df.parse("03-05-2021")),
                                                        getServiceLine(df.parse("04-05-2022"), df.parse("04-05-2022")));
    	
    	MemberClaimResponse memberClaimResponse01 = MemberClaimResponse.builder()
    			.claimId("11113344")
    			.contractId("12345678")
    			.memberID("01")
    			.receivedTs(Timestamp.valueOf("2022-06-01 00:00:00.527"))
    			.serviceLines(member01ServiceLine)
    			.build();
    	
    	MemberClaimResponse memberClaimResponse02 = MemberClaimResponse.builder()
    			.claimId("22223344")
    			.contractId("12345678")
    			.memberID("02")
    			.serviceLines(member02ServiceLine)
    			.receivedTs(Timestamp.valueOf("2022-06-05 00:00:00.527"))
    			.build();
    	
    	MemberClaimResponse response = mockPCPValidatorService.calculateLatestClaim(List.of(memberClaimResponse01, memberClaimResponse02));
    	assertEquals(response.getClaimId(), "22223344");
    }
    
    
    private ServiceLine getServiceLine(Date fromDate,Date thruDate) {
    	ServiceLine serviceLine = ServiceLine.builder()
    			.fromDate(fromDate)
    			.thruDate(thruDate)
    			.build();
    	return serviceLine;
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
