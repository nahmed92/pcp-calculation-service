package com.deltadental.mtv.sync.interservice;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
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
import com.deltadental.pcp.calculation.error.RestTemplateErrorHandler;

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

    @Autowired
    private RestTemplateErrorHandler restTemplateErrorHandler;

	public List<MemberClaimResponse> memberClaim(List<String> claimId) {
		log.info("START MTVSyncServiceClient.memberClaim");
		List<MemberClaimResponse> memberClaimResponse = null;
		try {
			String updatePCPMemberEndPoint = pcpMtvSyncServiceEndpoint.concat(MTVSyncServiceConstants.MEMBER_CLAIM);
			UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(updatePCPMemberEndPoint);
			final Map<String, String> params = new HashMap<>();
			params.put("claim-ids", String.join(",",claimId));
			URI exclusionsUri = builder.buildAndExpand(params).toUri();
			log.info("Request uri : {} ", exclusionsUri);
			restTemplate.setErrorHandler(restTemplateErrorHandler);
			HttpHeaders headers = new HttpHeaders();
			HttpEntity<Object> requestEntity = new HttpEntity<Object>(headers);
			ResponseEntity<List<MemberClaimResponse>> responseEntity = restTemplate.exchange(
					exclusionsUri,
					HttpMethod.GET,
					requestEntity,
					new ParameterizedTypeReference<List<MemberClaimResponse>>(){});

			
			if(responseEntity != null && responseEntity.getBody() != null) {
				memberClaimResponse = responseEntity.getBody();
				log.info("Response for claim id {} is {} ",claimId, memberClaimResponse);
			}			
		} catch (RestClientException e) {
			log.error("Error calling MTV member claim for request claim id {}", claimId,e);
			throw PCPCalculationServiceErrors.MTV_SYNC_CLAIM_SERVICE_ERROR.createException();
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
            // set custom error handler
            restTemplate.setErrorHandler(restTemplateErrorHandler);
            ResponseEntity<ProviderAssignmentResponse> responseEntity = restTemplate.postForEntity(new URI(uriBuilder), request, ProviderAssignmentResponse.class);
            log.info("MTV Sync Service request {} and response {} for provider assignment", providerAssignmentRequest, responseEntity);
            if (responseEntity != null && responseEntity.getBody() != null) {
                providerAssignmentResponse = responseEntity.getBody();
                log.info("Response for pcp assignment request {} is {} ", providerAssignmentRequest, providerAssignmentResponse);
            }
        } catch (RestClientException | URISyntaxException e) {
            log.error("Error calling MTV provider assignment for request {}", providerAssignmentRequest, e);
            throw PCPCalculationServiceErrors.PROVIDER_ASSIGNMENT_SERVICE_ERROR.createException();
        }
        log.info("END MTVSyncServiceClient.providerAssignment()");
        return providerAssignmentResponse;
    }
}
