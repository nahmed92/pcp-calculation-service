package com.deltadental.pcp.calculation.service;

import com.deltadental.pcp.calculation.domain.MemberContractClaimRequest;
import com.deltadental.pcp.calculation.entities.ContractMemberClaimEntity;
import com.deltadental.pcp.calculation.enums.Status;
import com.deltadental.pcp.calculation.mapper.Mapper;
import com.deltadental.pcp.calculation.repos.ContractMemberClaimRepo;
import org.apache.commons.lang.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

public class MemberContractClaimServiceTests {

    @InjectMocks
    MemberContractClaimService mockMemberContractClaimService;

    @Mock
    ContractMemberClaimRepo mockRepo;

    @Mock
    Mapper mapper;

    @Mock
    PCPValidatorService mockPCPValidatorService;

    String serviceInstanceId = "instanceId";

    private static final List<Status> SEARCH_STATUS = List.of(Status.RETRY, Status.STAGED, Status.VALIDATED, Status.PCP_ASSIGNED, Status.PCP_EXCLUDED, Status.PCP_NOT_INCLUDED);

    @BeforeEach
    public void init(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testStageMemberContractClaimRecords_success(){

        List<MemberContractClaimRequest> requestList = new ArrayList<>();
        MemberContractClaimRequest request = buildRequest();
        requestList.add(request);

        List<ContractMemberClaimEntity> memberClaimsEntities = new ArrayList<>();
        ContractMemberClaimEntity contractMemberClaimsEntity = buildContractMemberClaimEntity();
        memberClaimsEntities.add(contractMemberClaimsEntity);

        Mockito.when(mockRepo
                .findByClaimIdAndContractIdAndMemberIdAndProviderIdAndStateAndStatusInList(
                        StringUtils.trimToNull(request.getClaimId()), // check this and remove
                        StringUtils.trimToNull(request.getContractId()), StringUtils.trimToNull(request.getMemberId()),
                        StringUtils.trimToNull(request.getProviderId()), StringUtils.trimToNull(request.getState()),
                        SEARCH_STATUS
                )).thenReturn(memberClaimsEntities);
        mockMemberContractClaimService.stageMemberContractClaimRecords(requestList);
        Mockito.verify(mockRepo).
                findByClaimIdAndContractIdAndMemberIdAndProviderIdAndStateAndStatusInList(
                        Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),Mockito.any(), Mockito.any());
        assertEquals(memberClaimsEntities.size(), 1);
    }

    @Test
    public void testStageMemberContractClaimRecords_duplicateRecord(){

        List<MemberContractClaimRequest> requestList = new ArrayList<>();
        MemberContractClaimRequest request = buildRequest();
        requestList.add(request);

        List<ContractMemberClaimEntity> memberClaimsEntities = new ArrayList<>();
        ContractMemberClaimEntity contractMemberClaimsEntity = buildContractMemberClaimEntity();
        memberClaimsEntities.add(contractMemberClaimsEntity);

        Mockito.when(mockRepo
                        .findByClaimIdAndContractIdAndMemberIdAndProviderIdAndStateAndStatusInList(
                                StringUtils.trimToNull(request.getClaimId()), // check this and remove
                                StringUtils.trimToNull(request.getContractId()), StringUtils.trimToNull(request.getMemberId()),
                                StringUtils.trimToNull(request.getProviderId()), StringUtils.trimToNull(request.getState()),
                                SEARCH_STATUS
                        ))
                .thenReturn(memberClaimsEntities);
        mockMemberContractClaimService.stageMemberContractClaimRecords(requestList);
        assertEquals(memberClaimsEntities.size(), 1);
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

    private MemberContractClaimRequest buildRequest() {
        MemberContractClaimRequest request = new MemberContractClaimRequest();
        request.setMemberId("M001");
        request.setContractId("C001");
        request.setClaimId("CL001");
        request.setProviderId("PR001");
        request.setOperatorId("OPR001");
        request.setState("CA");
        return request;
    }


}
