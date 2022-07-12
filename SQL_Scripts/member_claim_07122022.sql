EXEC sp_rename 'member_claim', 'member_claim_07122022';

CREATE TABLE dbo.member_claim (
	id varchar(200)  NOT NULL,
	billing_provider_id varchar(15)  NULL,
	business_level_4 varchar(10)  NULL,
	business_level_5 varchar(15)  NULL,
	business_level_6 varchar(15)  NULL,
	business_level_7 varchar(15)  NULL,
	claim_source varchar(5)  NULL,
	claim_status varchar(3)  NULL,
	claim_type varchar(3)  NULL,
	group_number varchar(7)  NULL,
	member_first_name varchar(50)  NULL,
	member_last_name varchar(50)  NULL,
	paid_at datetime NULL,
	person_id varchar(25)  NULL,
	received_at datetime NULL,
	resolved_at datetime NULL,
	services_number varchar(2)  NULL,
	creation_at datetime DEFAULT getdate() NULL,
	last_updated_at datetime DEFAULT getdate() NULL,
	operator_id varchar(25)  NULL,
	contract_member_claim_id varchar(200)  NOT NULL,
	contract_member_claim_sequence_id smallint NOT NULL,
	billing_prov_id varchar(255)  NULL,
	claim_id varchar(255)  NULL,
	contract_id varchar(255)  NULL,
	creation_ts datetime2 NULL,
	last_maint_ts datetime2 NULL,
	member_id varchar(255)  NULL,
	paid_ts datetime2 NULL,
	provider_id varchar(255)  NULL,
	received_ts datetime2 NULL,
	resolved_ts datetime2 NULL,
	CONSTRAINT pk_member_claim_id PRIMARY KEY (id)
);

-- dbo.member_claim foreign keys
ALTER TABLE dbo.member_claim ADD CONSTRAINT fk__member_claim FOREIGN KEY (contract_member_claim_id,contract_member_claim_sequence_id) REFERENCES dbo.contract_member_claim(id,sequence_id);