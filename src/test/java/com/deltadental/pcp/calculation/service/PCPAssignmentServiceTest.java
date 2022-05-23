package com.deltadental.pcp.calculation.service;

import com.deltadental.mtv.sync.interservice.MTVSyncServiceClient;
import com.deltadental.mtv.sync.interservice.dto.*;
import com.deltadental.pcp.calculation.entities.ContractMemberClaimEntity;
import com.deltadental.pcp.calculation.entities.MemberClaimEntity;
import com.deltadental.pcp.calculation.entities.MemberClaimServicesEntity;
import com.deltadental.pcp.calculation.entities.MemberProviderEntity;
import com.deltadental.pcp.calculation.interservice.PCPConfigData;
import com.deltadental.pcp.calculation.repos.MemberClaimRepo;
import com.deltadental.pcp.calculation.repos.MemberClaimServicesRepo;
import com.deltadental.pcp.calculation.repos.MemberProviderRepo;
import com.deltadental.pcp.search.interservice.PCPSearchServiceClient;
import com.deltadental.pcp.search.interservice.PCPValidateRequest;
import com.deltadental.pcp.search.interservice.PCPValidateResponse;
import com.deltadental.pcp.search.interservice.pojo.EnrolleeDetail;
import com.deltadental.pcp.search.interservice.pojo.PCPResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.awt.desktop.SystemEventListener;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class PCPAssignmentServiceTest {

    @InjectMocks
    PCPAssignmentService mockPCPAssignmentService;

    @Mock
    MTVSyncServiceClient mtvSyncService;

    @Mock
    PCPConfigData pcpConfigData;

    @Mock
    PCPSearchServiceClient pcpSearchService;

    @Mock
    MemberProviderRepo memberProviderRepo;

    @Mock
    MemberClaimServicesRepo memberClaimServicesRepo;

    @Mock
    MemberClaimRepo memberClaimRepo;

    @BeforeEach
    public void setup(){

        //MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testProcess_success(){
        ContractMemberClaimEntity contractMemberClaimEntity = buildContractMemberClaimEntity();
        //System.out.println(LocalDateTime.now().getNano());
        //System.out.println();
        MemberClaimResponse memberClaimResponse =buildMemberClaimResponse();
        //PCPValidateRequest pcpValidateRequest = buildPCPValidateRequest(memberClaimResponse, "20022022");
        PCPValidateResponse pcpValidateResponse = buildPCPValidateResponse();
        ProviderAssignmentResponse providerAssignmentResponse = buildProviderAssignmentResponse();
        Mockito.when(pcpConfigData.calculatePCPEffectiveDate()).thenReturn("20022022");
        Mockito.when(pcpSearchService.pcpValidate(Mockito.any())).thenReturn(pcpValidateResponse);
        Mockito.when(mtvSyncService.providerAssignment(Mockito.any())).thenReturn(providerAssignmentResponse);
        Mockito.when(memberClaimRepo.save(Mockito.any())).thenReturn(new MemberClaimEntity());
        Mockito.when(memberClaimServicesRepo.save(Mockito.any())).thenReturn(new MemberClaimServicesEntity());
        Mockito.when(memberProviderRepo.save(Mockito.any())).thenReturn(new MemberProviderEntity());
        mockPCPAssignmentService.process(contractMemberClaimEntity, memberClaimResponse);


    }

    private ProviderAssignmentResponse buildProviderAssignmentResponse() {
        return ProviderAssignmentResponse.builder()
                .returnCode("OK").build();
    }

    private PCPValidateRequest buildPCPValidateRequest(MemberClaimResponse memberClaimResponse, String pcpEffectiveDate) {
                return PCPValidateRequest.builder()
                        .contractId(memberClaimResponse.getContractId())
                        .lookAheadDays("90")
                        .memberType(memberClaimResponse.getMemberID())
                        .mtvPersonId(memberClaimResponse.getPersonId())
                        .pcpEffDate(pcpEffectiveDate)
                        .pcpEndDate("12-31-9999")
                        .providerId(memberClaimResponse.getProviderId())
                        .recordIdentifier("3")
                        .sourceSystem("DCM").build();
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
        entity.setId("ID0011");
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
        serviceLine.setServicePaidTs(ServicePaidTs.builder().nanos(LocalDateTime.now().getNano()).build());
        serviceLine.setServiceResolutionTs(ServiceResolutionTs.builder().nanos(LocalDateTime.now().getNano()).build());
        serviceLines.add(serviceLine);
        response.setPaidTs(PaidTs.builder().nanos(LocalDateTime.now().getNano()).build());
        response.setReceivedTs(ReceivedTs.builder().nanos(LocalDateTime.now().getNano()).build());
        response.setResolvedTs(ResolvedTs.builder().nanos(LocalDateTime.now().getNano()).build());
        response.setServiceLines(serviceLines);
        return response;
    }
}
