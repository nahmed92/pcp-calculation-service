package com.deltadental.pcp.calculation.entities;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.deltadental.pcp.calculation.enums.Status;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter
@Setter
@ToString
@AllArgsConstructor
@Builder
@Table(name = "contract_member_claim", schema = "dbo")
@Entity
public class ContractMemberClaimEntity implements Serializable {

    private static final long serialVersionUID = 1L;

	@EmbeddedId
	private ContractMemberClaimPK contractMemberClaimPK;
	
	@Column(name = "claim_id")
	private String claimId;
	
    @Column(name = "contract_id")
    private String contractId;

    @Column(name = "member_id")
    private String memberId;

    @Column(name = "provider_id")
    private String providerId;

    @Column(name = "state")
    private String state;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private Status status;

    @EqualsAndHashCode.Exclude
    @Column(name = "operator_id")
    private String operatorId;

    @EqualsAndHashCode.Exclude
    @Column(name = "instance_id")
    private String instanceId;

    @EqualsAndHashCode.Exclude
    @Column(name = "error_message")
    private String errorMessage;

    @EqualsAndHashCode.Exclude
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Timestamp creationAt;

    @EqualsAndHashCode.Exclude
    @UpdateTimestamp
    @Column(name = "last_updated_at")
    private Timestamp lastUpdatedAt;

    public String getContractIdAndMemberId() {
    	return this.contractId+"-"+this.memberId;
    }
}
