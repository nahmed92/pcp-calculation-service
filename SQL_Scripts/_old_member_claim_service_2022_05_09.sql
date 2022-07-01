
CREATE TABLE dbo.member_claim_service (
	id varchar(200) not null,
	claim_type varchar(2),
	encounter_flag varchar(1),
	expln_code varchar(10),
	procedure_code varchar(10),
	sequence_number varchar(5),
	service_number varchar(5),
	service_paid_at datetime null,
	service_resolution_at datetime null,
	created_at datetime null,
	last_update_at datetime null,
	operator_id varchar(255),
	member_claim_id varchar(200) null,
	constraint pk_member_claim_service_id primary key (id)
);