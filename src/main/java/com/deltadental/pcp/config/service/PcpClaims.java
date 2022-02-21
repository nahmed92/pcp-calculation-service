package com.deltadental.pcp.config.service;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
public class PcpClaims{

	private String codeValue;

	private String voidFlag;

	private Timestamp lastMaintTs;
	
	private String operatorID;
	
	private String product;
	
	private Integer objectVersionNo;
	
}
