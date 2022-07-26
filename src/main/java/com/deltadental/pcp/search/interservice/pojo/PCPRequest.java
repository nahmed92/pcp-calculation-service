package com.deltadental.pcp.search.interservice.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PCPRequest {

    private String contractId;
    private boolean autoAssignmentFlag;
    private List<PCPEnrollee> enrollees;
    private PrimaryEnrolleePCPInfo primaryEnrolleePCPInfo;
    private PCPRefineSearch pcpRefineSearch;

}
