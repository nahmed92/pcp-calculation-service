package com.deltadental.pcp.calculation.service;

import com.deltadental.pcp.calculation.interservice.PCPConfigData;
import com.deltadental.pcp.config.interservice.pojo.GroupRestrictions;
import com.deltadental.pcp.config.interservice.pojo.InclusionExclusion;
import com.deltadental.pcp.interservice.PCPConfigServiceClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PCPConfigDataTest {
    @InjectMocks
    PCPConfigData pcpConfigData;
    @Mock
    PCPConfigServiceClient mockPCPConfigService;

    @BeforeEach
    public void setup() {
        //MockitoAnnotations.initMocks(this);
        //pcpConfigData = new PCPConfigData();
    }

    //@DisplayName("Test provider inclusion")
    @Test
    void testProviderInclusionNotListed() {
        InclusionExclusion[] inclusionExclusions = new InclusionExclusion[1];
        InclusionExclusion inclusionExclusion = getInclusionExclusion();
        inclusionExclusions[0] = inclusionExclusion;
        when(mockPCPConfigService.inclusions(anyString())).thenReturn(inclusionExclusions);
        boolean inclusionFlag = pcpConfigData.isProviderInInclusionList("DC060381", "75394", "00003");
        assertFalse(inclusionFlag);
    }

    //@DisplayName("Test provider inclusion not listed")
    @Test
    void testProviderInclusion() {
        //pcpConfigData.setPcpConfigServiceClient(mockPCPConfigService);
        //pcpConfigData.setWashRuleCutoffDay(16);
        InclusionExclusion[] inclusionExclusions = new InclusionExclusion[1];
        InclusionExclusion inclusionExclusion = getInclusionExclusion();
        inclusionExclusion.setEffectiveDate("2022-04-01 00:00:00.0");
        inclusionExclusions[0] = inclusionExclusion;
        when(mockPCPConfigService.inclusions(anyString())).thenReturn(inclusionExclusions);
        boolean inclusionFlag = pcpConfigData.isProviderInInclusionList("DC060380", "75394", "00003");
        assertTrue(inclusionFlag);
    }

	
	/*
	@DisplayName("Test provider exclusion")
	@Test
	@Ignore
	void testProviderExclusion() {
		InclusionExclusion[] inclusionExclusions = new InclusionExclusion[1];
		InclusionExclusion inclusionExclusion = getInclusionExclusion(); 
		inclusionExclusions[0] = inclusionExclusion;
		when(mockPCPConfigService.exclusions(anyString())).thenReturn(inclusionExclusions);
		boolean inclusionFlag = pcpConfigData.isProviderInExclusionList("DC060380", "75394", "00003");
		assertFalse(inclusionFlag);
	}
	
	
	
	@DisplayName("Test provider exclusion not listed")
	@Test
	@Ignore
	void testProviderExclusionNotListed() {
		InclusionExclusion[] inclusionExclusions = new InclusionExclusion[1];
		InclusionExclusion inclusionExclusion = getInclusionExclusion(); 
		inclusionExclusion.setEffectiveDate("2022-04-01 00:00:00.0");
		inclusionExclusions[0] = inclusionExclusion;
		when(mockPCPConfigService.exclusions(anyString())).thenReturn(inclusionExclusions);
		boolean inclusionFlag = pcpConfigData.isProviderInExclusionList("DC026845", "78772", "00001");
		assertTrue(inclusionFlag);
	}
*/
    //@DisplayName("Test calculate effective date")
	/*@Test
	public void testCalculateEffectiveDate() {
		String effectiveDate = pcpConfigData.calculatePCPEffectiveDate();
		assertEquals(effectiveDate, calculatePCPEffectiveDate());
	}*/

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
        inclusionExclusion.setEffectiveDate("2022-07-04 00:00:00.0");
        GroupRestrictions groupRestrictions = new GroupRestrictions();
        groupRestrictions.setMasterContractId("DC060380");
        groupRestrictions.setDivisionId("00003");
        groupRestrictions.setGroupId("75394");
        inclusionExclusion.setGroupRestrictions(groupRestrictions);
        return inclusionExclusion;
    }
}
