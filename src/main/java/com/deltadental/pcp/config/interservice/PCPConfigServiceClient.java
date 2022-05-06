package com.deltadental.pcp.config.interservice;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.deltadental.pcp.calculation.error.PCPCalculationServiceErrors;
import com.deltadental.pcp.config.service.InclusionExclusion;
import static com.deltadental.pcp.config.service.PCPConfigServiceConstants.*;

import lombok.extern.slf4j.Slf4j;

@Service("pcpConfigService")
@Slf4j
public class PCPConfigServiceClient{

	@Value("${pcp.config.service.endpoint}")
	private String pcpConfigServiceEndpoint;

	@Autowired(required=true)
	private RestTemplate restTemplate;
	
	//FIXME: Generalize to one Method
	
	public String providerLookaheadDays() {
		log.info("START PCPConfigServiceClient.providerLookaheadDays");
		String providerLookaheadDaysEndPoint = pcpConfigServiceEndpoint.concat(PROVIDER_LOOKAHEAD_DAYS);
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(providerLookaheadDaysEndPoint);
		String uriBuilder = builder.build().encode().toUriString();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		try {
			ResponseEntity<String> responseEntity = restTemplate.exchange(new URI(uriBuilder), HttpMethod.GET,  new HttpEntity<>(headers), String.class);
			if(responseEntity.getStatusCode() == HttpStatus.OK) {
				return responseEntity.getBody();
			}else {
				log.error("Got {} response code from PCP Config API {} ",responseEntity.getStatusCode(),PROVIDER_LOOKAHEAD_DAYS);
			} 
		} catch (RestClientException | URISyntaxException e) {
			log.error("Unable to call PCP Config for API {} ",PROVIDER_LOOKAHEAD_DAYS,e);
			throw PCPCalculationServiceErrors.PCP_CONFIG_ERROR.createException(e);
		}
		log.info("END PCPConfigServiceClient.providerLookaheadDays");
		return null;
	}
	
	public String explanationCode() {
		log.info("START PCPConfigServiceClient.explanationcode");
		final String explanationCode = pcpConfigServiceEndpoint.concat(EXPLANATION_CODE);
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(explanationCode);
		String uriBuilder = builder.build().encode().toUriString();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		try {
			ResponseEntity<String> responseEntity = restTemplate.exchange(new URI(uriBuilder), HttpMethod.GET,  new HttpEntity<>(headers), String.class);
			if(responseEntity.getStatusCode() == HttpStatus.OK) {
				return responseEntity.getBody();
			}else {
				log.error("Got {} response code from PCP Config API {} ",responseEntity.getStatusCode(),EXPLANATION_CODE);
			}
		} catch (RestClientException | URISyntaxException e) {
			log.error("Unable to call PCP Config for API {} ",EXPLANATION_CODE,e);
			throw PCPCalculationServiceErrors.PCP_CONFIG_ERROR.createException(e);
		}
		log.info("END PCPConfigServiceClient.explanationcode");
		return null;
	}
	
	public String procedureCode() {
		log.info("START PCPConfigServiceClient.explanationcode");
		final String procedureCode = pcpConfigServiceEndpoint.concat(PROCEDURE_CODE);
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(procedureCode);
		String uriBuilder = builder.build().encode().toUriString();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		try {
			ResponseEntity<String> responseEntity = restTemplate.exchange(new URI(uriBuilder), HttpMethod.GET,  new HttpEntity<>(headers), String.class);
			if(responseEntity.getStatusCode() == HttpStatus.OK) {
				return responseEntity.getBody();
			}else {
				log.error("Got {} response code from PCP Config API {} ",responseEntity.getStatusCode(),PROCEDURE_CODE);
			}
		} catch (RestClientException | URISyntaxException e) {
			log.error("Unable to call PCP Config for API {} ",PROCEDURE_CODE,e);
			throw PCPCalculationServiceErrors.PCP_CONFIG_ERROR.createException();
		}
		log.info("END PCPConfigServiceClient.explanationcode");
		return null;
	}
	
	public String claimStatus() {
		log.info("START PCPConfigServiceClient.claimStatus");
		final String claimStatusUrl = pcpConfigServiceEndpoint.concat(CLAIM_STATUS);
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(claimStatusUrl);
		String uriBuilder = builder.build().encode().toUriString();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		try {
			ResponseEntity<String> responseEntity = restTemplate.exchange(new URI(uriBuilder), HttpMethod.GET,  new HttpEntity<>(headers), String.class);
			if(responseEntity.getStatusCode() == HttpStatus.OK) {
				return responseEntity.getBody();
			} else {
				log.error("Got {} response code from PCP Config API {} ",responseEntity.getStatusCode(),CLAIM_STATUS);
			}
		} catch (RestClientException | URISyntaxException e) {
			log.error("Unable to call PCP Config for API {} ",CLAIM_STATUS,e);
			throw PCPCalculationServiceErrors.PCP_CONFIG_ERROR.createException();
		}
		log.info("END PCPConfigServiceClient.claimStatus");
		return null;
	}
	
	public InclusionExclusion[] exclusions(String providerId) {
		log.info("START PCPConfigServiceClient.exclusions");
		final String exclusionsProviderUrl = pcpConfigServiceEndpoint.concat(EXCLUSIONS_PROVIDER);
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(exclusionsProviderUrl);
		Map<String, String> params = new HashMap<String, String>();
	    params.put("providerId", providerId);
	    URI exclusionsUri = builder.buildAndExpand(params).toUri();
	    log.info("exclusions uri : "+exclusionsUri);
	    HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);		
		try {
			ResponseEntity<InclusionExclusion[]> responseEntity = this.restTemplate.exchange(exclusionsUri, HttpMethod.GET, new HttpEntity<>(headers), InclusionExclusion[].class);
			if (null != responseEntity && responseEntity.getStatusCode() == HttpStatus.OK) {
				return responseEntity.getBody();
			}else {
				log.error("Got {} response code from PCP Config API {} ",responseEntity.getStatusCode(),EXCLUSIONS_PROVIDER);
			}
		} catch (RestClientException e) {
			log.error("Unable to call PCP Config for API {} ",EXCLUSIONS_PROVIDER,e);
			throw PCPCalculationServiceErrors.PCP_CONFIG_ERROR.createException();
		}		
		log.info("END PCPConfigServiceClient.exclusions");
		return new InclusionExclusion[0];
	}
	
	public InclusionExclusion[] inclusions(String providerId) {
		log.info("START PCPConfigServiceClient.inclusions");
		final String inclusionsProviderUrl = pcpConfigServiceEndpoint.concat(INCLUSIONS_PROVIDER);
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(inclusionsProviderUrl);
		Map<String, String> params = new HashMap<String, String>();
	    params.put("providerId", providerId);
	    URI inclusionsUri = builder.buildAndExpand(params).toUri();
	    log.info("exclusions uri : "+inclusionsUri);
	    HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);		
		try {
			ResponseEntity<InclusionExclusion[]> responseEntity = this.restTemplate.exchange(inclusionsUri, HttpMethod.GET, new HttpEntity<>(headers), InclusionExclusion[].class);
			if (null != responseEntity && responseEntity.getStatusCode() == HttpStatus.OK) {
				return responseEntity.getBody();
			}else {
				log.error("Got {} response code from PCP Config API {} ",responseEntity.getStatusCode(),INCLUSIONS_PROVIDER);
			}
		} catch (RestClientException e) {
			log.error("Unable to call PCP Config for API {} ",INCLUSIONS_PROVIDER,e);
			throw PCPCalculationServiceErrors.PCP_CONFIG_ERROR.createException();
		}		
		log.info("END PCPConfigServiceClient.inclusions");
		return new InclusionExclusion[0];
	}
}
