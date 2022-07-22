package com.deltadental.mtv.sync.interservice;

import com.deltadental.mtv.sync.interservice.dto.MemberClaimResponse;
import com.deltadental.mtv.sync.interservice.dto.ProviderAssignmentRequest;
import com.deltadental.mtv.sync.interservice.dto.ProviderAssignmentResponse;
import com.deltadental.pcp.calculation.error.PCPCalculationServiceErrors;
import com.deltadental.pcp.calculation.error.RestTemplateErrorHandler;
import com.deltadental.pcp.security.HttpHeaderBuilder;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@NoArgsConstructor
@Data
@Service("mtvSyncService")
@Slf4j
public class MTVSyncServiceClient {

    @Value("${pcp.mtv.sync.service.url}")
    private String pcpMtvSyncServiceUrl;
    
    private static final String MEMBER_CLAIM = "/claims/{claim-ids}";
    
	private static final String PROVIDER_ASSIGNMENT = "/provider-assignment";
	
    @Autowired
    private RestTemplateErrorHandler restTemplateErrorHandler;
    
    @Autowired
	@Qualifier("securedRestTemplate")
	private RestTemplate restTemplate;

    @Autowired
	private HttpHeaderBuilder httpHeaderBuilder;

	public List<MemberClaimResponse> memberClaim(List<String> claimId) {
		log.info("START MTVSyncServiceClient.memberClaim");
		List<MemberClaimResponse> memberClaimResponse = List.of();
		if(CollectionUtils.isNotEmpty(claimId)) {
		try {
			String updatePCPMemberEndPoint = pcpMtvSyncServiceUrl.concat(MEMBER_CLAIM);
			UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(updatePCPMemberEndPoint);
			final Map<String, String> params = new HashMap<>();
			params.put("claim-ids", String.join(",",claimId));
			URI memberClaimUrl = builder.buildAndExpand(params).toUri();
			log.info("Member Claim Request uri : {} ", memberClaimUrl);
			restTemplate.setErrorHandler(restTemplateErrorHandler);
			HttpHeaders headers = httpHeaderBuilder.createHttpSecurityHeaders();
			HttpEntity<Object> requestEntity = new HttpEntity<>(headers);
			ResponseEntity<List<MemberClaimResponse>> responseEntity = restTemplate.exchange(
					memberClaimUrl,
					HttpMethod.GET,
					requestEntity,
					new ParameterizedTypeReference<List<MemberClaimResponse>>(){});

			
			if(responseEntity.getBody() != null) {
				memberClaimResponse = responseEntity.getBody();
				log.info("Response for claim id {} is {} ",claimId, memberClaimResponse);
			}			
		} catch (RestClientException e) {
			log.error("Error calling MTV member claim for request claim id {}", claimId,e);
			throw PCPCalculationServiceErrors.MTV_SYNC_CLAIM_SERVICE_ERROR.createException();
		}
	}
		log.info("END MTVSyncServiceClient.memberClaim()");
		return memberClaimResponse;
	}

    public ProviderAssignmentResponse providerAssignment(ProviderAssignmentRequest providerAssignmentRequest) {
        log.info("START MTVSyncServiceClient.providerAssignment()");
        ProviderAssignmentResponse providerAssignmentResponse = null;
        try {
            String updatePCPMemberEndPoint = pcpMtvSyncServiceUrl.concat(PROVIDER_ASSIGNMENT);
            UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(updatePCPMemberEndPoint);
            String uriBuilder = builder.build().encode().toUriString();
            HttpHeaders headers = httpHeaderBuilder.createHttpSecurityHeaders();
            HttpEntity<ProviderAssignmentRequest> request = new HttpEntity<>(providerAssignmentRequest, headers);
            // set custom error handler
            restTemplate.setErrorHandler(restTemplateErrorHandler);
            log.info("Provider Assignment Request uri : {} ", uriBuilder);
            ResponseEntity<ProviderAssignmentResponse> responseEntity = restTemplate.postForEntity(new URI(uriBuilder), request, ProviderAssignmentResponse.class);
            log.info("MTV Sync Service request {} and response {} for provider assignment", providerAssignmentRequest, responseEntity);
            if (responseEntity.getBody() != null) {
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
