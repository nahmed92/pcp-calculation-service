package com.deltadental.pcp.calculation.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;
import java.util.Locale;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import com.deltadental.pcp.config.service.GroupRestrictions;
import com.deltadental.pcp.config.service.InclusionExclusion;
import com.deltadental.pcp.config.service.PCPConfigService;

@TestInstance(Lifecycle.PER_CLASS)
@RunWith(MockitoJUnitRunner.class)
class PCPConfigDataTest {

	@Mock
	private PCPConfigService mockPCPConfigService;
	
	private PCPConfigData pcpConfigData;
	
	@BeforeAll
	public void initialize() {
		MockitoAnnotations.initMocks(this);
		pcpConfigData = new PCPConfigData();
		pcpConfigData.setPcpConfigService(mockPCPConfigService);
	}
	
	@DisplayName("Test provider inclusion")
	@Test
	void testProviderInclusion() {
		InclusionExclusion[] inclusionExclusions = new InclusionExclusion[1];
		InclusionExclusion inclusionExclusion = getInclusionExclusion(); 
		inclusionExclusions[0] = inclusionExclusion;
		when(mockPCPConfigService.inclusions(anyString())).thenReturn(inclusionExclusions);
		boolean inclusionFlag = pcpConfigData.isProviderInInclusionList("DC026845", "78672", "00001");
		assertTrue(inclusionFlag);
	}
	
	@DisplayName("Test provider inclusion not listed")
	@Test
	void testProviderInclusionNotListed() {
		InclusionExclusion[] inclusionExclusions = new InclusionExclusion[1];
		InclusionExclusion inclusionExclusion = getInclusionExclusion(); 
		inclusionExclusion.setEffectiveDate("2022-04-01 00:00:00.0");
		inclusionExclusions[0] = inclusionExclusion;
		when(mockPCPConfigService.inclusions(anyString())).thenReturn(inclusionExclusions);
		boolean inclusionFlag = pcpConfigData.isProviderInInclusionList("DC026845", "78662", "00001");
		assertFalse(inclusionFlag);
	}
	
	@DisplayName("Test provider exclusion")
	@Test
	void testProviderExclusion() {
		InclusionExclusion[] inclusionExclusions = new InclusionExclusion[1];
		InclusionExclusion inclusionExclusion = getInclusionExclusion(); 
		inclusionExclusions[0] = inclusionExclusion;
		when(mockPCPConfigService.exclusions(anyString())).thenReturn(inclusionExclusions);
		boolean inclusionFlag = pcpConfigData.isProviderInExclusionList("DC026845", "78672", "00001");
		assertFalse(inclusionFlag);
	}
	
	@DisplayName("Test provider exclusion not listed")
	@Test
	void testProviderExclusionNotListed() {
		InclusionExclusion[] inclusionExclusions = new InclusionExclusion[1];
		InclusionExclusion inclusionExclusion = getInclusionExclusion(); 
		inclusionExclusion.setEffectiveDate("2022-04-01 00:00:00.0");
		inclusionExclusions[0] = inclusionExclusion;
		when(mockPCPConfigService.exclusions(anyString())).thenReturn(inclusionExclusions);
		boolean inclusionFlag = pcpConfigData.isProviderInExclusionList("DC026845", "78772", "00001");
		assertTrue(inclusionFlag);
	}

	@DisplayName("Test calculate effective date")
	@Test
	void testCalculateEffectiveDate() {
		String effectiveDate = pcpConfigData.calculatePCPEffectiveDate();
		assertEquals(effectiveDate, calculatePCPEffectiveDate());
	}
	
	private String calculatePCPEffectiveDate() {
		ZoneId defaultZoneId = ZoneId.of("America/Los_Angeles");
		final DateFormat formatter = new SimpleDateFormat("MM-dd-yyyy", Locale.US);
		LocalDate now = LocalDate.now(defaultZoneId);		
		int currentDateDay = now.getDayOfMonth();
        if (currentDateDay < 16) {
        	LocalDate firstDayOfMonth = LocalDate.now(defaultZoneId).with(TemporalAdjusters.firstDayOfMonth());
        	Date firstDateOfMonth = Date.from(firstDayOfMonth.atStartOfDay(defaultZoneId).toInstant());
        	return formatter.format(firstDateOfMonth);
        } else {
        	LocalDate firstDayOfNextMonth = LocalDate.now(defaultZoneId).with(TemporalAdjusters.firstDayOfNextMonth());
        	Date firstDateOfNextMonth = Date.from(firstDayOfNextMonth.atStartOfDay(defaultZoneId).toInstant());
        	return formatter.format(firstDateOfNextMonth);
        }
	}
	
	private InclusionExclusion getInclusionExclusion() {
		InclusionExclusion inclusionExclusion = new InclusionExclusion();
		inclusionExclusion.setEffectiveDate("2022-05-01 00:00:00.0");
		GroupRestrictions groupRestrictions = new GroupRestrictions();
		groupRestrictions.setMasterContractId("DC026845");
		groupRestrictions.setDivisionId("00001");
		groupRestrictions.setGroupId("78672");
		inclusionExclusion.setGroupRestrictions(groupRestrictions);
		return inclusionExclusion;
	}
}
