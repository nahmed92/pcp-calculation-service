package com.deltadental.pcp.interservice;

import com.deltadental.pcp.calculation.error.PCPCalculationServiceErrors;
import com.deltadental.pcp.config.interservice.pojo.InclusionExclusion;
import com.deltadental.platform.common.annotation.aop.MethodExecutionTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import static com.deltadental.pcp.config.interservice.pojo.PCPConfigServiceConstants.*;

@Service("pcpConfigService")
@Slf4j
public class PCPConfigServiceClient {

    @Value("${pcp.config.service.endpoint}")
    private String pcpConfigServiceEndpoint;

    @Autowired(required = true)
    private RestTemplate restTemplate;

    private static final String LOOK_A_HEAD_DAYS_90 = "90";

    @MethodExecutionTime
    public String getPCPConfigData(String serviceEndPoint) {
        log.info("START PCPConfigServiceClient.pcpConfigData {} ", serviceEndPoint);
        String providerLookaheadDaysEndPoint = pcpConfigServiceEndpoint.concat(serviceEndPoint);
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(providerLookaheadDaysEndPoint);
        String uriBuilder = builder.build().encode().toUriString();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        try {
            ResponseEntity<String> responseEntity = restTemplate.exchange(new URI(uriBuilder), HttpMethod.GET, new HttpEntity<>(headers), String.class);
            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                return responseEntity.getBody();
            } else {
                log.error("Got {} response code from PCP Config API {} ", responseEntity.getStatusCode(), serviceEndPoint);
            }
        } catch (RestClientException | URISyntaxException e) {
            log.error("Unable to call PCP Config for API {} ", serviceEndPoint, e);
            throw PCPCalculationServiceErrors.PCP_CONFIG_ERROR.createException(e);
        }
        log.info("END PCPConfigServiceClient.pcpConfigData {} ", serviceEndPoint);
        return null;
    }

    @MethodExecutionTime
    public String providerLookaheadDays() {
        log.info("START PCPConfigServiceClient.providerLookaheadDays");
        String providerLookaheadDaysEndPoint = pcpConfigServiceEndpoint.concat(PROVIDER_LOOKAHEAD_DAYS);
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(providerLookaheadDaysEndPoint);
        String uriBuilder = builder.build().encode().toUriString();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String returnValue = LOOK_A_HEAD_DAYS_90;
        try {
            ResponseEntity<String> responseEntity = restTemplate.exchange(new URI(uriBuilder), HttpMethod.GET, new HttpEntity<>(headers), String.class);
            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                returnValue = responseEntity.getBody();
            } else {
                log.error("Got {} response code from PCP Config API {} ", responseEntity.getStatusCode(), PROVIDER_LOOKAHEAD_DAYS);
            }
        } catch (Exception e) {
            log.error("Unable to call PCP Config for API {} ", PROVIDER_LOOKAHEAD_DAYS, e);
//			throw PCPCalculationServiceErrors.PCP_CONFIG_ERROR.createException(e);
        }
        log.info("Returning provider Look a head days {}", returnValue);
        log.info("END PCPConfigServiceClient.providerLookaheadDays");
        return returnValue;
    }

    @MethodExecutionTime
    public InclusionExclusion[] exclusions(String providerId) {
        log.info("START PCPConfigServiceClient.exclusions");
        final String exclusionsProviderUrl = pcpConfigServiceEndpoint.concat(EXCLUSIONS_PROVIDER);
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(exclusionsProviderUrl);
        Map<String, String> params = new HashMap<String, String>();
        params.put("providerId", providerId);
        URI exclusionsUri = builder.buildAndExpand(params).toUri();
        log.info("exclusions uri : " + exclusionsUri);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        try {
            ResponseEntity<InclusionExclusion[]> responseEntity = this.restTemplate.exchange(exclusionsUri, HttpMethod.GET, new HttpEntity<>(headers), InclusionExclusion[].class);
            if (null != responseEntity && responseEntity.getStatusCode() == HttpStatus.OK) {
                return responseEntity.getBody();
            } else {
                log.error("Got {} response code from PCP Config API {} ", responseEntity.getStatusCode(), EXCLUSIONS_PROVIDER);
            }
        } catch (RestClientException e) {
            log.error("Unable to call PCP Config for API {} ", EXCLUSIONS_PROVIDER, e);
            throw PCPCalculationServiceErrors.PCP_CONFIG_ERROR.createException();
        }
        log.info("END PCPConfigServiceClient.exclusions");
        return new InclusionExclusion[0];
    }

    @MethodExecutionTime
    public InclusionExclusion[] inclusions(String providerId) {
        log.info("START PCPConfigServiceClient.inclusions");
        final String inclusionsProviderUrl = pcpConfigServiceEndpoint.concat(INCLUSIONS_PROVIDER);
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(inclusionsProviderUrl);
        Map<String, String> params = new HashMap<String, String>();
        params.put("providerId", providerId);
        URI inclusionsUri = builder.buildAndExpand(params).toUri();
        log.info("exclusions uri : " + inclusionsUri);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        try {
            ResponseEntity<InclusionExclusion[]> responseEntity = this.restTemplate.exchange(inclusionsUri, HttpMethod.GET, new HttpEntity<>(headers), InclusionExclusion[].class);
            if (null != responseEntity && responseEntity.getStatusCode() == HttpStatus.OK) {
                return responseEntity.getBody();
            } else {
                log.error("Got {} response code from PCP Config API {} ", responseEntity.getStatusCode(), INCLUSIONS_PROVIDER);
            }
        } catch (RestClientException e) {
            log.error("Unable to call PCP Config for API {} ", INCLUSIONS_PROVIDER, e);
            throw PCPCalculationServiceErrors.PCP_CONFIG_ERROR.createException();
        }
        log.info("END PCPConfigServiceClient.inclusions");
        return new InclusionExclusion[0];
    }
}
