
CREATE TABLE dbo.member_claim (
	id varchar(200) NOT NULL,
	billing_provider_id varchar(15),
	business_level_4 varchar(10),
	business_level_5 varchar(15),
	business_level_6 varchar(15),
	business_level_7 varchar(15),
	claim_source varchar(5),
	claim_status varchar(3),
	claim_type varchar(3),
	group_number varchar(7),
	member_first_name varchar(50),
	member_last_name varchar(50),
	paid_at datetime NULL,
	person_id varchar(25),
	received_at datetime NULL,
	resolved_at datetime NULL,
	services_number varchar(2),
	creation_at datetime DEFAULT getdate() NULL,
	last_updated_at datetime DEFAULT getdate() NULL,
	operator_id varchar(25),
	contract_member_claim_id varchar(200) NOT NULL,
	contract_member_claim_sequence_id smallint NOT NULL,
	CONSTRAINT pk_member_claim_id PRIMARY KEY (id)
	
  FOREIGN KEY (contract_member_claim_id, contract_member_claim_sequence_id) REFERENCES contract_member_claim
  (id, sequence_id));
);

