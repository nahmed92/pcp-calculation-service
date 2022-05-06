package com.deltadental.mtv.sync.interservice;

import java.net.URI;
import java.net.URISyntaxException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.deltadental.mtv.sync.interservice.dto.MemberClaimRequest;
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

	public MemberClaimResponse memberClaim(MemberClaimRequest memberClaimRequest) {
		log.info("START MTVSyncServiceClient.memberClaim");
		MemberClaimResponse responseEntity = null;
		try {
			String updatePCPMemberEndPoint = pcpMtvSyncServiceEndpoint.concat(MTVSyncServiceConstants.MEMBER_CLAIM);
			UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(updatePCPMemberEndPoint);
			String uriBuilder = builder.build().encode().toUriString();
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<MemberClaimRequest> request = new HttpEntity<MemberClaimRequest>(memberClaimRequest, headers);
			responseEntity = restTemplate.postForObject(new URI(uriBuilder), request, MemberClaimResponse.class);

		} catch (RestClientException | URISyntaxException e) {
			log.error("Error calling MTV member claim for request {}", memberClaimRequest);
			throw PCPCalculationServiceErrors.PCP_MTV_SYNC_SERVICE_ERROR.createException();
		}
		log.info("END MTVSyncServiceClient.memberClaim()");
		return responseEntity;

	}

	public ProviderAssignmentResponse providerAssignment(ProviderAssignmentRequest providerAssignmentRequest) {
		log.info("START MTVSyncServiceClient.providerAssignment");
		ProviderAssignmentResponse responseEntity = null;
		try {
			String updatePCPMemberEndPoint = pcpMtvSyncServiceEndpoint
					.concat(MTVSyncServiceConstants.PROVIDER_ASSIGNMENT);
			UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(updatePCPMemberEndPoint);
			String uriBuilder = builder.build().encode().toUriString();
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<ProviderAssignmentRequest> request = new HttpEntity<>(providerAssignmentRequest, headers);
			responseEntity = restTemplate.postForObject(new URI(uriBuilder), request, ProviderAssignmentResponse.class);

		} catch (RestClientException | URISyntaxException e) {
			log.error("Error calling MTV member claim for request {}", providerAssignmentRequest);
			throw PCPCalculationServiceErrors.PCP_MTV_SYNC_SERVICE_ERROR.createException();
		}
		log.info("END MTVSyncServiceClient.providerAssignment()");
		return responseEntity;
	}
}
