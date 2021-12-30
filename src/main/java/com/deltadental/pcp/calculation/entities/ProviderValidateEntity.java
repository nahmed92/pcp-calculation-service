package com.deltadental.pcp.calculation.entities;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Immutable;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@Entity
@Immutable
@Table(name = "provider_validate", schema = "dbo")
public class ProviderValidateEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id", nullable = false, unique = true)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "pcp_identifier", nullable = false)
	private String pcpIdentifier;

	@Column(name = "zip_code", nullable = false)
	private String zipCode;

	@Column(name = "member_type", nullable = false)
	private String memberType;

	@Column(name = "source_system", nullable = false)
	private String sourceSystem;

	@Column(name = "provider_validation", nullable = false)
	private String providerValidation;

	@Column(name = "creation_ts")
	private Timestamp crationTs;

	@Column(name = "last_maint_ts")
	private Timestamp lastMaintTs;
}
