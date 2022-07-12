
EXEC sp_rename 'contract_member_claim', 'contract_member_claim_old';

CREATE TABLE dbo.contract_member_claim (
	id varchar(200) NOT NULL,
	sequence_id smallint NOT NULL,
	claim_id varchar(25) NOT NULL,
	contract_id varchar(15) NOT NULL,
	member_id varchar(2) NOT NULL,
	provider_id varchar(15) NOT NULL,
	state varchar(2) NOT NULL,
	status varchar(25) NULL,
	operator_id varchar(25) NULL,
	created_at datetime DEFAULT getdate() NULL,
	last_updated_at datetime DEFAULT getdate() NULL,
	instance_id varchar(25) NULL,
	error_message varchar(4096) NULL,
	CONSTRAINT pk_contract_member_claim_id_sequence_id PRIMARY KEY (id,sequence_id)
);
CREATE INDEX idx_instance_id ON dbo.contract_member_claim (  instance_id ASC  ) ;
CREATE INDEX idx_claim_contract_provider_member_state ON dbo.contract_member_claim (  claim_id ASC  , contract_id ASC  , provider_id ASC  , member_id ASC  , state ASC  );