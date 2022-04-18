package com.deltadental.pcp.config.service;

import java.net.URI;
import java.net.URISyntaxException;

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

import lombok.extern.slf4j.Slf4j;

@Service("pcpConfigService")
@Slf4j
public class PCPConfigService {

	@Value("${pcp.config.service.endpoint}")
	private String pcpConfigServiceEndpoint;

	@Autowired(required=true)
	private RestTemplate restTemplate;
	
	public String providerLookaheadDays() {
		log.info("START PCPConfigService.providerLookaheadDays");
		String providerLookaheadDaysEndPoint = pcpConfigServiceEndpoint.concat(PCPConfigServiceConstants.PROVIDER_LOOKAHEAD_DAYS);
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(providerLookaheadDaysEndPoint);
		String uriBuilder = builder.build().encode().toUriString();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		try {
			ResponseEntity<String> responseEntity = restTemplate.exchange(new URI(uriBuilder), HttpMethod.GET,  new HttpEntity<>(headers), String.class);
			if(responseEntity.getStatusCode() == HttpStatus.OK) {
				return responseEntity.getBody();
			} 
		} catch (RestClientException | URISyntaxException e) {
			String stacktrace = ExceptionUtils.getStackTrace(e);
			throw PCPCalculationServiceErrors.PCP_CONFIG_ERROR.createException(stacktrace);
		}
		log.info("END PCPConfigService.providerLookaheadDays");
		return null;
	}
	
	public String explanationCode() {
		log.info("START PCPConfigService.explanationcode");
		final String explanationCode = pcpConfigServiceEndpoint.concat(PCPConfigServiceConstants.EXPLANATION_CODE);
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(explanationCode);
		String uriBuilder = builder.build().encode().toUriString();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		try {
			ResponseEntity<String> responseEntity = restTemplate.exchange(new URI(uriBuilder), HttpMethod.GET,  new HttpEntity<>(headers), String.class);
			if(responseEntity.getStatusCode() == HttpStatus.OK) {
				return responseEntity.getBody();
			} 
		} catch (RestClientException | URISyntaxException e) {
			String stacktrace = ExceptionUtils.getStackTrace(e);
			throw PCPCalculationServiceErrors.PCP_CONFIG_ERROR.createException(stacktrace);
		}
		log.info("END PCPConfigService.explanationcode");
		return null;
	}
	
	public String procedureCode() {
		log.info("START PCPConfigService.explanationcode");
		final String procedureCode = pcpConfigServiceEndpoint.concat(PCPConfigServiceConstants.PROCEDURE_CODE);
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(procedureCode);
		String uriBuilder = builder.build().encode().toUriString();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		try {
			ResponseEntity<String> responseEntity = restTemplate.exchange(new URI(uriBuilder), HttpMethod.GET,  new HttpEntity<>(headers), String.class);
			if(responseEntity.getStatusCode() == HttpStatus.OK) {
				return responseEntity.getBody();
			}
		} catch (RestClientException | URISyntaxException e) {
			String stacktrace = ExceptionUtils.getStackTrace(e);
			throw PCPCalculationServiceErrors.PCP_CONFIG_ERROR.createException(stacktrace);
		}
		log.info("END PCPConfigService.explanationcode");
		return null;
	}
	
	public String claimStatus() {
		log.info("START PCPConfigService.claimStatus");
		final String claimStatusUrl = pcpConfigServiceEndpoint.concat(PCPConfigServiceConstants.CLAIM_STATUS);
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(claimStatusUrl);
		String uriBuilder = builder.build().encode().toUriString();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		try {
			ResponseEntity<String> responseEntity = restTemplate.exchange(new URI(uriBuilder), HttpMethod.GET,  new HttpEntity<>(headers), String.class);
			if(responseEntity.getStatusCode() == HttpStatus.OK) {
				return responseEntity.getBody();
			} 
		} catch (RestClientException | URISyntaxException e) {
			String stacktrace = ExceptionUtils.getStackTrace(e);
			throw PCPCalculationServiceErrors.PCP_CONFIG_ERROR.createException(stacktrace);
		}
		log.info("END PCPConfigService.claimStatus");
		return null;
	}
}
