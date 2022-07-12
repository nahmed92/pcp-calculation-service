ALTER TABLE dbo.member_claim_service DROP CONSTRAINT member_claim_service_pk_id;

EXEC sp_rename 'member_claim_service', 'member_claim_service_07122022';

CREATE TABLE dbo.member_claim_service (
	id varchar(200) NOT NULL,
	claim_type varchar(2) NULL,
	encounter_flag varchar(1) NULL,
	expln_code varchar(10) NULL,
	procedure_code varchar(10) NULL,
	sequence_number varchar(5) NULL,
	service_number varchar(5) NULL,
	service_paid_at datetime NULL,
	service_resolution_at datetime NULL,
	creation_at datetime NULL,
	last_update_at datetime NULL,
	operator_id varchar(255) NULL,
	member_claim_id varchar(200) NULL,
	from_date date NULL,
	thru_date date NULL,
	received_timestamp datetime NULL,
	CONSTRAINT pk_member_claim_service_id PRIMARY KEY (id)
);