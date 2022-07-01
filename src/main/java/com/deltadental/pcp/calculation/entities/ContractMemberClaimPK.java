package com.deltadental.pcp.calculation.entities;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

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
