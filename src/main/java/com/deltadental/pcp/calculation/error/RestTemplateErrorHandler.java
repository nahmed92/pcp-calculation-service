package com.deltadental.pcp.calculation.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;

@Component
@Slf4j
public class RestTemplateErrorHandler implements ResponseErrorHandler {

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return new DefaultResponseErrorHandler().hasError(response);
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        log.info("START : RestTemplateErrorHandler.handleError");
        if (response.getStatusCode().series() == HttpStatus.Series.SERVER_ERROR) {
            // handle 5xx errors
            // raw http status code e.g `500`
            log.error("Raw http status code {}.", response.getRawStatusCode());

            // http status code e.g. `500 INTERNAL_SERVER_ERROR`
            log.error("http status code {}.", response.getStatusCode());

        } else if (response.getStatusCode().series() == HttpStatus.Series.CLIENT_ERROR) {
            // handle 4xx errors
            // raw http status code e.g `404`
            log.error("Raw http status code {}.", response.getRawStatusCode());

            // http status code e.g. `404 NOT_FOUND`
            log.error("http status code {}.", response.getStatusCode());

            // get response body
            log.error("Response body {}.", response.getBody());

            // get http headers
            HttpHeaders headers = response.getHeaders();
            log.error("Content-Type {}", headers.get("Content-Type"));
            log.error("Server {}", headers.get("Server"));
        }
        log.info("END : RestTemplateErrorHandler.handleError");
    }
}