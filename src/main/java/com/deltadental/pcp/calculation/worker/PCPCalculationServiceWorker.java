package com.deltadental.pcp.calculation.worker;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.deltadental.pcp.calculation.constants.PCPCalculationServiceConstants;
import com.deltadental.pcp.calculation.entities.ContractMemberClaimEntity;
import com.deltadental.pcp.calculation.entities.PCPCalculationActivityEntity;
import com.deltadental.pcp.calculation.repos.ContractMemberClaimRepo;
import com.deltadental.pcp.calculation.repos.PCPCalculationServiceActivityRepo;
import com.deltadental.pcp.calculation.util.MemberClaimUtils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PCPCalculationServiceWorker {

    @Value("${pcp.assignment.process.workers.count:5}")
    private Integer pcpAssignmentProcessWorkersCount;

    @Value("${service.instance.id}")
    private String serviceInstanceId;

    private Executor executor;

    @Autowired
    private ContractMemberClaimRepo contractMemberClaimsRepo;

    @Autowired
    private PCPCalculationServiceActivityRepo activityRepo;

    @Autowired
    private MemberClaimUtils memberClaimUtils;
    
    @PostConstruct
    public void setup() {
        log.info("START PCPCalculationServiceWorker.setup()");
        log.info("PCP Assignment Workers Count {}.", pcpAssignmentProcessWorkersCount);
        executor = Executors.newFixedThreadPool(pcpAssignmentProcessWorkersCount);
        log.info("END PCPCalculationServiceWorker.setup()");
    }

    @Lookup
    PCPAssignmentTask createTask() {
        return null;
    }

    public void submitTask(List<ContractMemberClaimEntity> entities) {
        log.info("START PCPCalculationServiceWorker.submitTask()");
        if (CollectionUtils.isNotEmpty(entities)) {
            log.info("Submiting to task for contract member claim {}", memberClaimUtils.getClaimIds(entities));                    
        	PCPAssignmentTask pcpAssignmentTask = createTask();
			pcpAssignmentTask.setContractMemberClaimEntities(entities);
			executor.execute(pcpAssignmentTask);
        }
        log.info("END PCPCalculationServiceWorker.submitTask()");
    }

    public void processPCPAssignmentRequests() {
        log.info("START PCPCalculationServiceWorker.processPCPAssignmentRequests()");
        List<ContractMemberClaimEntity> contractMemberClaimsEntities = contractMemberClaimsRepo.findByInstanceIdWhereStatusInList(serviceInstanceId, PCPCalculationServiceConstants.SEARCH_STATUS_VALIDATE_PENDING);
        if (CollectionUtils.isNotEmpty(contractMemberClaimsEntities)) {
            long startTime = System.currentTimeMillis();            
            log.info("Processing total {} pcp assignment requests on service instance {} ", contractMemberClaimsEntities.size(), serviceInstanceId);
                try {
                	Map<String, List<ContractMemberClaimEntity>> contractMemberClaimEntityMap = contractMemberClaimsEntities.stream().distinct().collect(Collectors.groupingBy(ContractMemberClaimEntity::getContractIdAndMemberId));                	
                	contractMemberClaimEntityMap.values().forEach(this::submitTask);
                } catch (Exception e) {
                    log.error("Exception processing pcp assignment request for contract member claim {} ", e);
                }
            long endTime = System.currentTimeMillis();
            long minutes = TimeUnit.MILLISECONDS.toMinutes((endTime - startTime));
            log.info(" Thread Name + {}  taken to complete process :  {} minute[s]", Thread.currentThread().getName(), minutes);
            PCPCalculationActivityEntity activity = PCPCalculationActivityEntity.builder()
                    .id(UUID.randomUUID().toString())
                    .instanceId(serviceInstanceId)
                    .numOfRecords(contractMemberClaimsEntities.size())
                    .timeToProcess(minutes)
                    .startTime(new Timestamp(startTime))
                    .endTime(new Timestamp(endTime))
                    .build();
            activityRepo.save(activity);
        } else {
            log.info("No pending requests for pcp assignment on this service instance id {} .", serviceInstanceId);
        }
        log.info("END PCPCalculationServiceWorker.processPCPAssignmentRequests()");
    }
    
}