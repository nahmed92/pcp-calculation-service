package com.deltadental.pcp.calculation.util;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.deltadental.mtv.sync.interservice.dto.MemberClaimResponse;
import com.deltadental.pcp.calculation.entities.ContractMemberClaimEntity;


@Component
public class MemberClaimUtils {
	
	public MemberClaimResponse calculateLatestClaim(List<MemberClaimResponse> members) {
		if(members.size()==1) {
			return members.get(0);
		}
		for (MemberClaimResponse memberClaim : members) {
			Date maxFromDate = memberClaim.getServiceLines().stream().map(u -> u.getFromDate()).max(Date::compareTo)
					.get();
			Date maxThruDate = memberClaim.getServiceLines().stream().map(u -> u.getThruDate()).max(Date::compareTo)
					.get();
			memberClaim.setFromDate(maxFromDate);
			memberClaim.setThruDate(maxThruDate);
		}
		
		MemberClaimResponse memberClaimResponse = null;
		Optional<MemberClaimResponse> collectData = members.stream().collect(Collectors.maxBy(Comparator
				.comparing(MemberClaimResponse::getFromDate).thenComparing(MemberClaimResponse::getThruDate)
				.thenComparing(MemberClaimResponse::getReceivedTs)));
		if (collectData.isPresent()) {
			memberClaimResponse = collectData.get();
		}

		return memberClaimResponse;
	}

	public List<String> getClaimIds(List<ContractMemberClaimEntity> contractMemberClaimEntities) {
		return contractMemberClaimEntities.stream().map(i -> i.getClaimId()).collect(Collectors.toList());
	}
}
