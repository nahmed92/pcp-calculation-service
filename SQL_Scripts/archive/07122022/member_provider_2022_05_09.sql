

CREATE TABLE dbo.member_provider (
	id varchar(200) not null,
	claim_status varchar(3) ,
	pcp_effective_date date ,
	reason_code varchar(7) ,
	source_system varchar(5) ,
	status varchar(15) ,
	creation_at datetime2 default getdate() null,
	last_updated_at datetime2 default getdate() null,
	operator_id varchar(25) ,
	contract_member_claim_id varchar(200) not null,
	contract_id varchar(15) ,
	member_id varchar(2) ,
	person_id varchar(25) ,
	provider_id varchar(15) ,
	provider_qualifier_id varchar(15) ,
	practice_location_id varchar(15) ,
	provider_contract_id varchar(15) ,
	business_level_assn_id varchar(15) 
);


