package com.deltadental.mtv.sync.interservice;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.deltadental.mtv.sync.interservice.pojo.MemberClaimRequest;
import com.deltadental.mtv.sync.interservice.pojo.MemberClaimResponse;
import com.deltadental.mtv.sync.interservice.pojo.ProviderAssignmentRequest;
import com.deltadental.mtv.sync.interservice.pojo.ProviderAssignmentResponse;
import com.deltadental.pcp.calculation.error.PCPCalculationServiceErrors;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@NoArgsConstructor
@Data
@Service("mtvSyncService")
@Slf4j
public class MTVSyncServiceClient {
	
	@Value("${pcp.mtv.sync.service.endpoint}")
	private String pcpMtvSyncServiceEndpoint;

	@Autowired(required=true)
	private RestTemplate restTemplate;
	
	public MemberClaimResponse memberClaim(MemberClaimRequest memberClaimRequest) {
		log.info("START MTVSyncServiceClient.memberClaim");
		String updatePCPMemberEndPoint = pcpMtvSyncServiceEndpoint.concat(MTVSyncServiceConstants.MEMBER_CLAIM);
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(updatePCPMemberEndPoint);
		String uriBuilder = builder.build().encode().toUriString();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<MemberClaimRequest> request = new HttpEntity<MemberClaimRequest>(memberClaimRequest, headers);
		try {
			MemberClaimResponse responseEntity = restTemplate.postForObject(new URI(uriBuilder), request, MemberClaimResponse.class);
			return responseEntity;
		} catch (RestClientException | URISyntaxException e) {
			String stacktrace = ExceptionUtils.getStackTrace(e);
			throw PCPCalculationServiceErrors.PCP_MTV_SYNC_SERVICE_ERROR.createException(stacktrace);
		}
		
	}

	public ProviderAssignmentResponse providerAssignment(ProviderAssignmentRequest providerAssignmentRequest) {
		log.info("START MTVSyncServiceClient.providerAssignment");
		String updatePCPMemberEndPoint = pcpMtvSyncServiceEndpoint.concat(MTVSyncServiceConstants.PROVIDER_ASSIGNMENT);
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(updatePCPMemberEndPoint);
		String uriBuilder = builder.build().encode().toUriString();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<ProviderAssignmentRequest> request = new HttpEntity<ProviderAssignmentRequest>(providerAssignmentRequest, headers);
		try {
			ProviderAssignmentResponse responseEntity = restTemplate.postForObject(new URI(uriBuilder), request, ProviderAssignmentResponse.class);
			return responseEntity;
		} catch (RestClientException | URISyntaxException e) {
			String stacktrace = ExceptionUtils.getStackTrace(e);
			throw PCPCalculationServiceErrors.PCP_MTV_SYNC_SERVICE_ERROR.createException(stacktrace);
		}		
	}
}
