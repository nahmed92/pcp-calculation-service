package com.deltadental.pcp.calculation.service;

import com.deltadental.mtv.sync.interservice.dto.MemberClaimResponse;
import com.deltadental.mtv.sync.interservice.dto.ServiceLine;
import com.deltadental.pcp.calculation.util.MemberClaimUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MemberClaimUtilsTest {
	
	private MemberClaimUtils memberClaimUtills;
	
	@BeforeEach
	void setUp() throws Exception {
		memberClaimUtills = new MemberClaimUtils();
	}
	
	@Test
    public void testCalculateLatestClaim() throws Exception {
    	DateFormat df = new SimpleDateFormat("MM-dd-yyyy", Locale.US);
    	List<ServiceLine> member01ServiceLine = List.of(getServiceLine(df.parse("03-04-2022"), df.parse("03-04-2022")),
    			                                        getServiceLine(df.parse("03-05-2022"), df.parse("03-05-2022")),
    	                                                getServiceLine(df.parse("04-05-2022"), df.parse("04-05-2022")));
    	
    	List<ServiceLine> member02ServiceLine = List.of(getServiceLine(df.parse("03-04-2021"), df.parse("03-04-2021")),
                                                        getServiceLine(df.parse("03-05-2021"), df.parse("03-05-2021")),
                                                        getServiceLine(df.parse("03-06-2022"), df.parse("03-06-2022")));
    	
    	MemberClaimResponse memberClaimResponse01 = MemberClaimResponse.builder()
    			.claimId("11113344")
    			.contractId("12345678")
    			.memberID("01")
    			.receivedTs(Timestamp.valueOf("2022-06-01 00:00:00.527"))
    			.serviceLines(member01ServiceLine)
    			.build();
    	
    	MemberClaimResponse memberClaimResponse02 = MemberClaimResponse.builder()
    			.claimId("22223344")
    			.contractId("12345678")
    			.memberID("02")
    			.serviceLines(member02ServiceLine)
    			.receivedTs(Timestamp.valueOf("2022-06-25 00:00:00.527"))
    			.build();
    	
    	MemberClaimResponse response = memberClaimUtills.calculateLatestClaim(List.of(memberClaimResponse01, memberClaimResponse02));
    	assertEquals(response.getClaimId(), "11113344");
    }
    
    @Test
    public void testCalculateLatestClaimThathasLatestThruAndOldFromDate() throws Exception {
    	DateFormat df = new SimpleDateFormat("MM-dd-yyyy", Locale.US);
    	List<ServiceLine> member01ServiceLine = List.of(getServiceLine(df.parse("03-04-2022"), df.parse("03-04-2022")),
    			                                        getServiceLine(df.parse("03-05-2022"), df.parse("03-05-2022")),
    	                                                getServiceLine(df.parse("04-05-2021"), df.parse("04-05-2022")));
    	
    	List<ServiceLine> member02ServiceLine = List.of(getServiceLine(df.parse("03-04-2021"), df.parse("03-04-2021")),
                                                        getServiceLine(df.parse("03-05-2021"), df.parse("03-05-2021")),
                                                        getServiceLine(df.parse("03-06-2022"), df.parse("03-06-2022")));
    	
    	MemberClaimResponse memberClaimResponse01 = MemberClaimResponse.builder()
    			.claimId("11113344")
    			.contractId("12345678")
    			.memberID("01")
    			.receivedTs(Timestamp.valueOf("2022-06-25 00:00:00.527"))
    			.serviceLines(member01ServiceLine)
    			.build();
    	
    	MemberClaimResponse memberClaimResponse02 = MemberClaimResponse.builder()
    			.claimId("22223344")
    			.contractId("12345678")
    			.memberID("02")
    			.serviceLines(member02ServiceLine)
    			.receivedTs(Timestamp.valueOf("2022-06-06 00:00:00.527"))
    			.build();
    	
    	MemberClaimResponse response = memberClaimUtills.calculateLatestClaim(List.of(memberClaimResponse01, memberClaimResponse02));
    	assertEquals(response.getClaimId(), "22223344");
    }
    
    @Test
    public void testCalculateLatestClaimWhenTwoMaxDateAreSame() throws Exception {
    	DateFormat df = new SimpleDateFormat("MM-dd-yyyy", Locale.US);
    	List<ServiceLine> member01ServiceLine = List.of(getServiceLine(df.parse("03-04-2022"), df.parse("03-04-2022")),
    			                                        getServiceLine(df.parse("03-05-2022"), df.parse("03-05-2022")),
    	                                                getServiceLine(df.parse("04-05-2022"), df.parse("04-05-2022")));
    	
    	List<ServiceLine> member02ServiceLine = List.of(getServiceLine(df.parse("03-04-2021"), df.parse("03-04-2021")),
                                                        getServiceLine(df.parse("03-05-2021"), df.parse("03-05-2021")),
                                                        getServiceLine(df.parse("04-05-2022"), df.parse("04-05-2022")));
    	
    	MemberClaimResponse memberClaimResponse01 = MemberClaimResponse.builder()
    			.claimId("11113344")
    			.contractId("12345678")
    			.memberID("01")
    			.receivedTs(Timestamp.valueOf("2022-06-01 00:00:00.527"))
    			.serviceLines(member01ServiceLine)
    			.build();
    	
    	MemberClaimResponse memberClaimResponse02 = MemberClaimResponse.builder()
    			.claimId("22223344")
    			.contractId("12345678")
    			.memberID("02")
    			.serviceLines(member02ServiceLine)
    			.receivedTs(Timestamp.valueOf("2022-06-05 00:00:00.527"))
    			.build();
    	
    	MemberClaimResponse response = memberClaimUtills.calculateLatestClaim(List.of(memberClaimResponse01, memberClaimResponse02));
    	assertEquals(response.getClaimId(), "22223344");
    }
    
    
    private ServiceLine getServiceLine(Date fromDate,Date thruDate) {
    	ServiceLine serviceLine = ServiceLine.builder()
    			.fromDate(fromDate)
    			.thruDate(thruDate)
    			.build();
    	return serviceLine;
    }


}
