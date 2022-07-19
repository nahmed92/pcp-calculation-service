package com.deltadental.pcp.calculation.util;

import com.deltadental.mtv.sync.interservice.dto.MemberClaimResponse;
import com.deltadental.mtv.sync.interservice.dto.ServiceLine;
import com.deltadental.pcp.calculation.entities.ContractMemberClaimEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Component
@Slf4j
public class MemberClaimUtils {
	
	public MemberClaimResponse calculateLatestClaim(List<MemberClaimResponse> members) {
		log.info("START MemberClaimUtils.calculateLatestClaim()");
		log.info("Members claims response size {}", members.size());
		if(members.size()==1) {
			return members.get(0);
		}
		for (MemberClaimResponse memberClaim : members) {
			Optional<Date> optionalMaxFromDate = memberClaim.getServiceLines().stream().map(ServiceLine::getFromDate).max(Date::compareTo);
			Optional<Date> optionalMaxThruDate = memberClaim.getServiceLines().stream().map(ServiceLine::getThruDate).max(Date::compareTo);
			optionalMaxFromDate.ifPresent(memberClaim::setFromDate);
			optionalMaxThruDate.ifPresent(memberClaim::setThruDate);
		}
		
		MemberClaimResponse memberClaimResponse = null;
		Optional<MemberClaimResponse> optionalMemberClaimResponse = members.stream().max(Comparator
				.comparing(MemberClaimResponse::getFromDate).thenComparing(MemberClaimResponse::getThruDate)
				.thenComparing(MemberClaimResponse::getReceivedTs));

		if (optionalMemberClaimResponse.isPresent()) {
			memberClaimResponse = optionalMemberClaimResponse.get();
		} else {
			log.warn("Returning null for member claim responses {} ", members);
		}
		log.info("Returning latest claim {} ",memberClaimResponse);
		log.info("END MemberClaimUtils.calculateLatestClaim()");
		return memberClaimResponse;
	}

	public List<String> getClaimIds(List<ContractMemberClaimEntity> contractMemberClaimEntities) {
		return contractMemberClaimEntities.stream().map(ContractMemberClaimEntity::getClaimId).distinct().collect(Collectors.toList());
	}
}
