package com.deltadental.pcp.calculation.service;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

import org.bouncycastle.asn1.cms.TimeStampAndCRL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.deltadental.pcp.calculation.controller.Contract;
import com.deltadental.pcp.calculation.entities.ProviderValidateEntity;
import com.deltadental.pcp.calculation.error.PCPCalculationServiceErrors;
import com.deltadental.pcp.calculation.repos.ProviderValidateRepo;
import com.deltadental.pcp.search.service.PCPSearchService;
import com.deltadental.pcp.search.service.pojos.PCPAssignmentResponse;
import com.deltadental.pcp.search.service.pojos.PCPResponse;
import com.deltadental.pcp.search.service.pojos.PcpAssignmentRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@NoArgsConstructor
@Slf4j
public class PCPCalculationService {

	@Autowired
	private PCPSearchService pcpSearchService;

	@Autowired
	private ProviderValidateRepo providerValidateRepo;

	@Autowired
	private ObjectMapper mapper;

	public String validateProvider() {
		log.info("START PCPCalculationService.validateProvider");
		PcpAssignmentRequest pcpAssignmentRequest = null;
		try {
			pcpAssignmentRequest = mapper.readerFor(PcpAssignmentRequest.class)
					.readValue(getClass().getClassLoader().getResourceAsStream("providerValidateRequest.json"));
		} catch (IOException e) {
			throw PCPCalculationServiceErrors.INTERNAL_SERVER_ERROR.createException(e.getMessage());
		}
		PCPAssignmentResponse pcpAssignmentResponse = pcpSearchService.validateProvider(pcpAssignmentRequest);
		if (null == pcpAssignmentResponse) {
			throw PCPCalculationServiceErrors.PROVIDER_NOT_VALIDATED.createException("Provider Validation Failed!");
		}
		List<PCPResponse> pcpResponses = pcpAssignmentResponse.getPcpResponses();
		if (pcpResponses.isEmpty()) {
			throw PCPCalculationServiceErrors.PROVIDER_NOT_VALIDATED.createException("Provider Validation Failed!");
		}

		ProviderValidateEntity providerValidateEntity = new ProviderValidateEntity();
		providerValidateEntity.setMemberType("01");
		providerValidateEntity.setPcpIdentifier("DC052910");
		providerValidateEntity.setSourceSystem("EMA");
		providerValidateEntity.setZipCode("78251");
		providerValidateEntity.setProviderValidation("PROVIDER VALIDATED");
		providerValidateRepo.save(providerValidateEntity);
		// TODO: save record into DB
		log.info("END PCPCalculationService.validateProvider");
		return "PROVIDER VALIDATED";
	}

	public List<Contract> setAssginmentDate(List<Contract> contracts) {
		contracts.stream().forEach(contract -> {
			int currentDateDay = LocalDate.now().getDayOfMonth();
			if (currentDateDay >= 1 && currentDateDay <= 16) {
				contract.setAssignmentDate(Timestamp.valueOf(LocalDateTime.now().withDayOfMonth(1)));
				log.info("setting assignment Date..{}", Timestamp.valueOf(LocalDateTime.now().withDayOfMonth(1)));
			} else {
				contract.setAssignmentDate(
						Timestamp.valueOf(LocalDateTime.now().with(TemporalAdjusters.firstDayOfNextMonth())));
				log.info("setting assignment Date..{}",
						Timestamp.valueOf(LocalDateTime.now().with(TemporalAdjusters.firstDayOfNextMonth())));
			}

		});
		return contracts;
	}
}
