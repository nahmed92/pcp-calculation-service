EXEC sp_rename 'member_provider', 'member_provider_07122022';

CREATE TABLE dbo.member_provider (
	id varchar(200) NOT NULL,
	claim_status varchar(3) NULL,
	pcp_effective_date date NULL,
	reason_code varchar(7) NULL,
	source_system varchar(5) NULL,
	status varchar(15) NULL,
	creation_at datetime2 DEFAULT getdate() NULL,
	last_updated_at datetime2 DEFAULT getdate() NULL,
	operator_id varchar(25) NULL,
	contract_id varchar(15) NULL,
	member_id varchar(2) NULL,
	person_id varchar(25) NULL,
	provider_id varchar(15) NULL,
	provider_qualifier_id varchar(15) NULL,
	practice_location_id varchar(15) NULL,
	provider_contract_id varchar(15) NULL,
	business_level_assn_id varchar(15) NULL,
	contract_member_claim_id varchar(200) NOT NULL,
	contract_member_claim_sequence_id smallint NOT NULL,
	claim_id varchar(255) NULL,
	creation_ts datetime2 NULL,
	last_maint_ts datetime2 NULL,
	pcp_end_date varchar(255) NULL,
	pcp_identifier varchar(255) NULL,
	reason_cd varchar(255) NULL,
	zip_code varchar(255) NULL
	CONSTRAINT pk_member_provider_id PRIMARY KEY (id)
);


-- dbo.member_provider foreign keys
ALTER TABLE dbo.member_provider ADD CONSTRAINT fk__member_provider FOREIGN KEY (contract_member_claim_id,contract_member_claim_sequence_id) REFERENCES dbo.contract_member_claim(id,sequence_id);