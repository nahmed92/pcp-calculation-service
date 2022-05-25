package com.deltadental.pcp.search.interservice;

import java.net.URI;
import java.net.URISyntaxException;

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

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@NoArgsConstructor
@Data
@Service("pcpSearchService")
@Slf4j
public class PCPSearchServiceClient {

	@Value("${pcp.search.service.endpoint}")
	private String pcpSearchServiceEndpoint;
	
	public static String PCP_VALIDATION = "/pcp/validate";

	@Autowired(required = true)
	private RestTemplate restTemplate;

	public PCPValidateResponse pcpValidate(PCPValidateRequest pcpValidateRequest) {
		log.info("START PCPSearchServiceClient.validateProvider");
		String providerValidateEndPoint = pcpSearchServiceEndpoint.concat(PCP_VALIDATION);
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(providerValidateEndPoint);
		String uriBuilder = builder.build().encode().toUriString();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		try {
			ResponseEntity<PCPValidateResponse> responseEntity = restTemplate.exchange(new URI(uriBuilder), HttpMethod.POST,  new HttpEntity<>(pcpValidateRequest, headers), PCPValidateResponse.class);
			if(responseEntity.getStatusCode() == HttpStatus.OK) {
				return responseEntity.getBody();
			}
		} catch (RestClientException | URISyntaxException e) {
			log.error("Error calling PCP search service pcp validate for request {}", pcpValidateRequest);
			throw PCPCalculationServiceErrors.PCP_VALIDATE_SERVICE_ERROR.createException();
		}
		log.info("END PCPSearchServiceClient.validateProvider");
		return null;
	}
}
