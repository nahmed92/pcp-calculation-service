package com.deltadental.mtv.sync.interservice;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.deltadental.mtv.sync.interservice.pojo.MemberClaimRequest;
import com.deltadental.mtv.sync.interservice.pojo.MemberClaimResponse;
import com.deltadental.mtv.sync.interservice.pojo.ProviderAssignmentRequest;
import com.deltadental.mtv.sync.interservice.pojo.ProviderAssignmentResponse;
import com.deltadental.mtv.sync.interservice.pojo.RetrieveContract;
import com.deltadental.mtv.sync.interservice.pojo.RetrieveContractResponse;
import com.deltadental.mtv.sync.interservice.pojo.RetrieveEligibilitySummary;
import com.deltadental.mtv.sync.interservice.pojo.RetrieveEligibilitySummaryResponse;
import com.deltadental.mtv.sync.interservice.pojo.UpdatePCPRequest;
import com.deltadental.mtv.sync.interservice.pojo.UpdatePCPResponse;
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
	
	public RetrieveContractResponse retrieveContract(RetrieveContract retrieveContract) {
		log.info("START MTVSyncServiceClient.retrieveContract");
		String retrieveContractEndPoint = pcpMtvSyncServiceEndpoint.concat(MTVSyncServiceConstants.RETRIEVE_CONTRACT);
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(retrieveContractEndPoint);
		String uriBuilder = builder.build().encode().toUriString();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<RetrieveContract> request = new HttpEntity<RetrieveContract>(retrieveContract, headers);
		try {
			ResponseEntity<RetrieveContractResponse> retrieveContractResponse = restTemplate.postForEntity(new URI(uriBuilder), request, RetrieveContractResponse.class);
			return retrieveContractResponse.getBody();
		} catch (RestClientException | URISyntaxException e) {
			String stacktrace = ExceptionUtils.getStackTrace(e);
			throw PCPCalculationServiceErrors.PCP_MTV_SYNC_SERVICE_ERROR.createException(stacktrace);
		}
	}
	
	public RetrieveEligibilitySummaryResponse retrieveEligibilitySummary(RetrieveEligibilitySummary retrieveEligibilitySummary) {
		log.info("START MTVSyncServiceClient.retrieveEligibilitySummary");
		String retrieveEligibilitySummaryEndPoint = pcpMtvSyncServiceEndpoint.concat(MTVSyncServiceConstants.RETRIEVE_ELIGIBILITY_SUMMARY);
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(retrieveEligibilitySummaryEndPoint);
		String uriBuilder = builder.build().encode().toUriString();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<RetrieveEligibilitySummary> request = new HttpEntity<RetrieveEligibilitySummary>(retrieveEligibilitySummary, headers);
		try {
			ResponseEntity<RetrieveEligibilitySummaryResponse> responseEntity = restTemplate.postForEntity(new URI(uriBuilder),  request, RetrieveEligibilitySummaryResponse.class);
			return responseEntity.getBody();
		} catch (RestClientException | URISyntaxException e) {
			String stacktrace = ExceptionUtils.getStackTrace(e);
			throw PCPCalculationServiceErrors.PCP_MTV_SYNC_SERVICE_ERROR.createException(stacktrace);
		}
	}
	
	public UpdatePCPResponse updatePCPMember(UpdatePCPRequest updatePCP) {
		log.info("START MTVSyncServiceClient.updatePCPMember");
		String updatePCPMemberEndPoint = pcpMtvSyncServiceEndpoint.concat(MTVSyncServiceConstants.UPDATE_PCP_MEMBER);
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(updatePCPMemberEndPoint);
		String uriBuilder = builder.build().encode().toUriString();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<UpdatePCPRequest> request = new HttpEntity<UpdatePCPRequest>(updatePCP, headers);
		try {
			UpdatePCPResponse responseEntity = restTemplate.postForObject(new URI(uriBuilder), request, UpdatePCPResponse.class);
			return responseEntity;
		} catch (RestClientException | URISyntaxException e) {
			String stacktrace = ExceptionUtils.getStackTrace(e);
			throw PCPCalculationServiceErrors.PCP_MTV_SYNC_SERVICE_ERROR.createException(stacktrace);
		}
	}

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
