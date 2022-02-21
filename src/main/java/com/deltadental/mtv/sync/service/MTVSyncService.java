package com.deltadental.mtv.sync.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.deltadental.pcp.calculation.error.PCPCalculationServiceErrors;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@NoArgsConstructor
@Service("mtvSyncService")
@Slf4j
public class MTVSyncService {

	
	@Value("${pcp.mtv.sync.service.endpoint}")
	private String pcpMtvSyncServiceEndpoint;

	@Autowired(required=true)
	private RestTemplate restTemplate;
	
	public RetrieveContractResponse retrieveContract(RetrieveContract retrieveContract) {
		log.info("START MTVSyncService.retrieveContract");
		String retrieveContractEndPoint = pcpMtvSyncServiceEndpoint.concat(MTVSyncServiceConstants.RETRIEVE_CONTRACT);
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(retrieveContractEndPoint);
		String uriBuilder = builder.build().encode().toUriString();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		// Create the request body by wrapping
        // the object in HttpEntity 
        HttpEntity<RetrieveContract> request = new HttpEntity<RetrieveContract>(retrieveContract, headers);
		try {
			setMessageConverter(restTemplate);
			ResponseEntity<RetrieveContractResponse> retrieveContractResponse = restTemplate.postForEntity(new URI(uriBuilder), request, RetrieveContractResponse.class);
			return retrieveContractResponse.getBody();
		} catch (RestClientException e) {
			throw PCPCalculationServiceErrors.INTERNAL_SERVER_ERROR.createException(e.getMessage());
		} catch (URISyntaxException e) {
			throw PCPCalculationServiceErrors.INTERNAL_SERVER_ERROR.createException(e.getMessage());
		}
	}
	
	public RetrieveEligibilitySummaryResponse retrieveEligibilitySummary(RetrieveEligibilitySummary retrieveEligibilitySummary) {
		log.info("START MTVSyncService.retrieveEligibilitySummary");
		String retrieveEligibilitySummaryEndPoint = pcpMtvSyncServiceEndpoint.concat(MTVSyncServiceConstants.RETRIEVE_ELIGIBILITY_SUMMARY);
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(retrieveEligibilitySummaryEndPoint);
		String uriBuilder = builder.build().encode().toUriString();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<RetrieveEligibilitySummary> request = new HttpEntity<RetrieveEligibilitySummary>(retrieveEligibilitySummary, headers);
		try {
			setMessageConverter(restTemplate);
			ResponseEntity<RetrieveEligibilitySummaryResponse> responseEntity = restTemplate.postForEntity(new URI(uriBuilder),  request, RetrieveEligibilitySummaryResponse.class);
			return responseEntity.getBody();
		} catch (RestClientException e) {
			e.printStackTrace();
			throw PCPCalculationServiceErrors.INTERNAL_SERVER_ERROR.createException(e.getMessage());
		} catch (URISyntaxException e) {
			throw PCPCalculationServiceErrors.INTERNAL_SERVER_ERROR.createException(e.getMessage());
		}
	}
	
	
	public UpdatePCPResponse updatePCPMember(UpdatePCPRequest updatePCP) {
		log.info("START MTVSyncService.updatePCPMember");
		String updatePCPMemberEndPoint = pcpMtvSyncServiceEndpoint.concat(MTVSyncServiceConstants.UPDATE_PCP_MEMBER);
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(updatePCPMemberEndPoint);
		String uriBuilder = builder.build().encode().toUriString();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<UpdatePCPRequest> request = new HttpEntity<UpdatePCPRequest>(updatePCP, headers);
		UpdatePCPResponse responseEntity = null;
		try {
			setMessageConverter(restTemplate);
			responseEntity = restTemplate.postForObject(new URI(uriBuilder), request, UpdatePCPResponse.class);
		} catch (RestClientException e) {
			throw PCPCalculationServiceErrors.INTERNAL_SERVER_ERROR.createException(e.getMessage());
		} catch (URISyntaxException e) {
			throw PCPCalculationServiceErrors.INTERNAL_SERVER_ERROR.createException(e.getMessage());
		}
		log.info("END MTVSyncService.updatePCPMember");
		return responseEntity;
	}
	

	public MemberClaimResponse memberClaim(MemberClaimRequest memberClaimRequest) {
		log.info("START MTVSyncService.MemberClaim");
		String updatePCPMemberEndPoint = pcpMtvSyncServiceEndpoint.concat(MTVSyncServiceConstants.MEMBER_CLAIM);
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(updatePCPMemberEndPoint);
		String uriBuilder = builder.build().encode().toUriString();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<MemberClaimRequest> request = new HttpEntity<MemberClaimRequest>(memberClaimRequest, headers);
		MemberClaimResponse responseEntity = null;
		try {
			setMessageConverter(restTemplate);
			responseEntity = restTemplate.postForObject(new URI(uriBuilder), request, MemberClaimResponse.class);
		} catch (RestClientException e) {
			throw PCPCalculationServiceErrors.INTERNAL_SERVER_ERROR.createException(e.getMessage());
		} catch (URISyntaxException e) {
			throw PCPCalculationServiceErrors.INTERNAL_SERVER_ERROR.createException(e.getMessage());
		}
		log.info("END MTVSyncService.updatePCPMember");
		return responseEntity;
	}
	

	public ProviderAssignmentResponse providerAssignment(ProviderAssignmentRequest providerAssignmentRequest) {
		log.info("START MTVSyncService.updatePCPMember");
		String updatePCPMemberEndPoint = pcpMtvSyncServiceEndpoint.concat(MTVSyncServiceConstants.PROVIDER_ASSIGNMENT);
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(updatePCPMemberEndPoint);
		String uriBuilder = builder.build().encode().toUriString();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<ProviderAssignmentRequest> request = new HttpEntity<ProviderAssignmentRequest>(providerAssignmentRequest, headers);
		ProviderAssignmentResponse responseEntity = null;
		try {
			setMessageConverter(restTemplate);
			responseEntity = restTemplate.postForObject(new URI(uriBuilder), request, ProviderAssignmentResponse.class);
		} catch (RestClientException e) {
			throw PCPCalculationServiceErrors.INTERNAL_SERVER_ERROR.createException(e.getMessage());
		} catch (URISyntaxException e) {
			throw PCPCalculationServiceErrors.INTERNAL_SERVER_ERROR.createException(e.getMessage());
		}
		log.info("END MTVSyncService.updatePCPMember");
		return responseEntity;
	}

	private static void setMessageConverter(RestTemplate restTemplate) {
		List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();        
		MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
		converter.setSupportedMediaTypes(Collections.singletonList(MediaType.APPLICATION_JSON));  
		messageConverters.add(new FormHttpMessageConverter());
        messageConverters.add(new StringHttpMessageConverter());
		messageConverters.add(converter);  
		restTemplate.setMessageConverters(messageConverters); 
	}
}
