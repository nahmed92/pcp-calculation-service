package com.deltadental.pcp.calculation.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.deltadental.pcp.calculation.constants.PCPCalculationServiceConstants;
import com.deltadental.pcp.calculation.domain.MemberContractClaimRequest;
import com.deltadental.pcp.calculation.entities.ContractMemberClaimEntity;
import com.deltadental.pcp.calculation.mapper.Mapper;
import com.deltadental.pcp.calculation.repos.ContractMemberClaimRepo;
import com.deltadental.platform.common.annotation.aop.MethodExecutionTime;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MemberContractClaimService {

    @Autowired
    private ContractMemberClaimRepo repo;

    @Autowired
    private PCPValidatorService pcpValidatorService;

    @Autowired
    private Mapper mapper;

    @Value("${service.instance.id}")
    private String serviceInstanceId;

    @MethodExecutionTime
    @Transactional
    public List<ContractMemberClaimEntity> save(List<MemberContractClaimRequest> requests) {
        log.info("START MemberContractClaimService.save");
        List<ContractMemberClaimEntity> entities = List.of();
        if (CollectionUtils.isNotEmpty(requests)) {
            // FIXME:
            List<MemberContractClaimRequest> memberClaimRequests = getLatestMemberClaimsEntities(requests);
            if (CollectionUtils.isNotEmpty(memberClaimRequests)) {
                log.info("Inserting  {} ", memberClaimRequests);
                entities = mapper.map(memberClaimRequests, serviceInstanceId);
                // stage member contract
                repo.saveAll(entities);
            }
        }
        log.info("END MemberContractClaimService.save");
        return entities;
    }

    @MethodExecutionTime
    private List<MemberContractClaimRequest> getLatestMemberClaimsEntities(List<MemberContractClaimRequest> requests) {
        List<MemberContractClaimRequest> memberClaimsRequests = new ArrayList<>();
        for (MemberContractClaimRequest request : requests) {
            // FIXME:
            List<ContractMemberClaimEntity> memberClaimsEntities = repo.findByClaimIdAndContractIdAndMemberIdAndProviderIdAndStateAndStatusInList(
                    StringUtils.trimToNull(request.getClaimId()), // check this and remove
                    StringUtils.trimToNull(request.getContractId()),
                    StringUtils.trimToNull(request.getMemberId()),
                    StringUtils.trimToNull(request.getProviderId()),                    
                    StringUtils.trimToNull(request.getState()), PCPCalculationServiceConstants.SEARCH_STATUS_SAVE);
            if (CollectionUtils.isEmpty(memberClaimsEntities)) {
                memberClaimsRequests.add(request);
            } else {
                log.warn("Record already exists in contract member claims table : {} ", request);
            }
        }
        return memberClaimsRequests;
    }

    @MethodExecutionTime
    public void stageMemberContractClaimRecords(List<MemberContractClaimRequest> memberContractClaimRequests) {
        log.info("START MemberContractClaimService.stageMemberContractClaimRecords");
        List<ContractMemberClaimEntity> entities = save(memberContractClaimRequests);
//        if (CollectionUtils.isNotEmpty(entities)) {
//            pcpValidatorService.validateAndAssignPCP(entities);
//        }
        log.info("END MemberContractClaimService.stageMemberContractClaimRecords");
    }
}
