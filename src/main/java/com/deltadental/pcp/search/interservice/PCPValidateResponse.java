package com.deltadental.pcp.search.interservice;

import com.deltadental.pcp.search.interservice.pojo.PCPResponse;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PCPValidateResponse {

    private String sourceSystem;
    private String processStatusDescription;
    private String processStatusCode;
    private List<PCPResponse> pcpResponses;

}
