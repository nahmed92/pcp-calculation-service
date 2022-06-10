package com.deltadental.pcp.calculation.service;

import com.deltadental.pcp.calculation.domain.MemberContractClaimRequest;
import com.deltadental.pcp.calculation.entities.ContractMemberClaimEntity;
import com.deltadental.pcp.calculation.enums.Status;
import com.deltadental.pcp.calculation.mapper.Mapper;
import com.deltadental.pcp.calculation.repos.ContractMemberClaimRepo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

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

    private static final List<Status> SEARCH_STATUS = List.of(Status.RETRY, Status.STAGED, Status.VALIDATED, Status.PCP_ASSIGNED, Status.PCP_EXCLUDED, Status.PCP_NOT_INCLUDED);

    private void save(MemberContractClaimRequest request) {
        log.info("START MemberContractClaimService.save");
        List<ContractMemberClaimEntity> memberClaimsEntities = repo
                .findByClaimIdAndContractIdAndMemberIdAndProviderIdAndStateAndStatusInList(
                        StringUtils.trimToNull(request.getClaimId()), // check this and remove
                        StringUtils.trimToNull(request.getContractId()), StringUtils.trimToNull(request.getMemberId()),
                        StringUtils.trimToNull(request.getProviderId()), StringUtils.trimToNull(request.getState()),
                        SEARCH_STATUS);

        if (CollectionUtils.isEmpty(memberClaimsEntities)) {
            log.info("Inserting  {} ", request);
            ContractMemberClaimEntity contractMemberClaimsEntity = mapper.map(request, serviceInstanceId);
            repo.save(contractMemberClaimsEntity);
            pcpValidatorService.validateAndAssignPCP(contractMemberClaimsEntity);
        } else {
            log.warn("Record already exists in contract member claims table : {} ", request);
        }
        log.info("END MemberContractClaimService.save");
    }

    public void stageMemberContractClaimRecords(List<MemberContractClaimRequest> memberContractClaimRequests) {
        log.info("START MemberContractClaimService.stageMemberContractClaimRecords");
        if (CollectionUtils.isNotEmpty(memberContractClaimRequests)) {
            memberContractClaimRequests.forEach(i -> save(i));
        }
        log.info("END MemberContractClaimService.stageMemberContractClaimRecords");
    }

}
