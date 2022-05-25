package com.deltadental.mtv.sync.interservice;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.deltadental.mtv.sync.interservice.dto.MemberClaimResponse;
import com.deltadental.mtv.sync.interservice.dto.ProviderAssignmentRequest;
import com.deltadental.mtv.sync.interservice.dto.ProviderAssignmentResponse;
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

	@Autowired(required = true)
	private RestTemplate restTemplate;

	public MemberClaimResponse memberClaim(String claimId) {
		log.info("START MTVSyncServiceClient.memberClaim");
		MemberClaimResponse memberClaimResponse = null;
		try {
			String updatePCPMemberEndPoint = pcpMtvSyncServiceEndpoint.concat(MTVSyncServiceConstants.MEMBER_CLAIM);
			UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(updatePCPMemberEndPoint);
			final Map<String, String> params = new HashMap<>();
			params.put("claim-id", StringUtils.trimToNull(claimId));
			URI exclusionsUri = builder.buildAndExpand(params).toUri();
			log.info("Request uri : {} ", exclusionsUri);
			ResponseEntity<MemberClaimResponse> responseEntity = restTemplate.getForEntity(exclusionsUri, MemberClaimResponse.class);
			if(responseEntity.getStatusCode() != HttpStatus.OK) {
				log.error("Unable to retrive claim information for claim id {} and Response code {} ",claimId, responseEntity.getStatusCode());
				throw PCPCalculationServiceErrors.PCP_MTV_SYNC_SERVICE_ERROR.createException("Unable to retrive claim information for claim id {} and Response code {} ",claimId, responseEntity.getStatusCode());
			} else {
				memberClaimResponse = responseEntity.getBody();
				log.info("Response for claim id {} is {} ",claimId, memberClaimResponse);
			}
		} catch (RestClientException e) {
			e.printStackTrace();
			log.error("Error calling MTV member claim for request claim id {}", claimId);
			throw PCPCalculationServiceErrors.PCP_MTV_SYNC_SERVICE_ERROR.createException();
		}
		log.info("END MTVSyncServiceClient.memberClaim()");
		return memberClaimResponse;
	}

	public ProviderAssignmentResponse providerAssignment(ProviderAssignmentRequest providerAssignmentRequest) {
		log.info("START MTVSyncServiceClient.providerAssignment()");
		ProviderAssignmentResponse providerAssignmentResponse = null;
		try {
			String updatePCPMemberEndPoint = pcpMtvSyncServiceEndpoint.concat(MTVSyncServiceConstants.PROVIDER_ASSIGNMENT);
			UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(updatePCPMemberEndPoint);
			String uriBuilder = builder.build().encode().toUriString();
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<ProviderAssignmentRequest> request = new HttpEntity<>(providerAssignmentRequest, headers);
			ResponseEntity<ProviderAssignmentResponse> responseEntity = restTemplate.postForEntity(new URI(uriBuilder), request, ProviderAssignmentResponse.class);
			log.info("MTV Sync Service request {} and response {} for provider assignment", providerAssignmentRequest, responseEntity);
			if(responseEntity.getStatusCode() != HttpStatus.OK) {
				log.error("Unknown exception occured during provider assignment for provider assignment request {} and Response code {} ",providerAssignmentRequest, responseEntity.getStatusCode());
				throw PCPCalculationServiceErrors.PCP_MTV_SYNC_SERVICE_ERROR.createException("Unknown exception occured during provider assignment for provider assignment request {} and Response code {} ",providerAssignmentRequest, responseEntity.getStatusCode());
			} else {
				providerAssignmentResponse = responseEntity.getBody();
				log.info("Response for pcp assignment request {} is {} ",providerAssignmentRequest, providerAssignmentResponse);
			}
		} catch (RestClientException | URISyntaxException e) {
			log.error("Error calling MTV member claim for request {}", providerAssignmentRequest);
			throw PCPCalculationServiceErrors.PCP_MTV_SYNC_SERVICE_ERROR.createException();
		}
		log.info("END MTVSyncServiceClient.providerAssignment()");
		return providerAssignmentResponse;
	}
}
