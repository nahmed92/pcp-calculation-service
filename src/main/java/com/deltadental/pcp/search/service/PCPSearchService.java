package com.deltadental.pcp.search.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
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
import com.deltadental.pcp.search.service.pojos.PCPAssignmentResponse;
import com.deltadental.pcp.search.service.pojos.PcpAssignmentRequest;

import lombok.extern.slf4j.Slf4j;

@Service("pcpSearchService")
@Slf4j
public class PCPSearchService {

	@Value("${pcp.search.service.endpoint}")
	private String pcpSearchServiceEndpoint;

	@Autowired(required=true)
	private RestTemplate restTemplate;
	
	public PCPAssignmentResponse validateProvider(PcpAssignmentRequest pcpAssignmentRequest) {
		log.info("START PCPSearchService.validateProvider");
		String providerValidateEndPoint = pcpSearchServiceEndpoint.concat(PCPSearchServiceConstants.PROVIDER_VALIDATION);
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(providerValidateEndPoint);
		String uriBuilder = builder.build().encode().toUriString();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		ResponseEntity<PCPAssignmentResponse> responseEntity = null;
		try {
			setMessageConverter(restTemplate);
			responseEntity = restTemplate.exchange(new URI(uriBuilder), HttpMethod.POST,  new HttpEntity<>(pcpAssignmentRequest, headers), PCPAssignmentResponse.class);
		} catch (RestClientException e) {
			throw PCPCalculationServiceErrors.INTERNAL_SERVER_ERROR.createException(e.getMessage());
		} catch (URISyntaxException e) {
			throw PCPCalculationServiceErrors.INTERNAL_SERVER_ERROR.createException(e.getMessage());
		}
		if(responseEntity.getStatusCode() == HttpStatus.OK) {
			return responseEntity.getBody();
		} 
		log.info("END PCPSearchService.validateProvider");
		return null;
	}
	
	private static void setMessageConverter(RestTemplate restTemplate) {
		List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();        
		MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
		converter.setSupportedMediaTypes(Collections.singletonList(MediaType.ALL));  
		messageConverters.add(new FormHttpMessageConverter());
        messageConverters.add(new StringHttpMessageConverter());
		messageConverters.add(converter);  
		restTemplate.setMessageConverters(messageConverters); 
	}
}
