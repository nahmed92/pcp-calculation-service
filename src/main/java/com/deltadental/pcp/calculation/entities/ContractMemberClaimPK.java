package com.deltadental.pcp.calculation.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder
@EqualsAndHashCode
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class ContractMemberClaimPK implements Serializable{

	private static final long serialVersionUID = 1L;

	@Column(name = "id", insertable=false, updatable=false)
	private String id;

	@Column(name = "sequence_id")
	private Integer sequenceId;
}
