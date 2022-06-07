package com.deltadental.pcp.calculation.service;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import com.deltadental.pcp.calculation.domain.MemberContractClaimRequest;

@ExtendWith(MockitoExtension.class)
public class ExcelServiceTest {

    @InjectMocks
    ExcelService mockExcelService;

    @BeforeEach
    public void setup(){

        //file = file.
    }

    @Test
    public void testHasAttachment_true(){
        String TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        MultipartFile file = Mockito.mock(MultipartFile.class);
        Mockito.when(file.getContentType()).thenReturn(TYPE);
        boolean expectedResult = mockExcelService.isExcelFormat(file);
        Assertions.assertEquals(true, expectedResult);
    }

    @Test
    public void testHasAttachment_false(){
        String TYPE = "application/any";
        MultipartFile file = Mockito.mock(MultipartFile.class);
        Mockito.when(file.getContentType()).thenReturn(TYPE);
        boolean expectedResult = mockExcelService.isExcelFormat(file);
        Assertions.assertEquals(false, expectedResult);
    }

    @Test
    public void testExtractPCPMemberClaimsData_emptyfile() throws Exception{
        File f = new File("src/test/resources/data/ExcelServiceTestData_empty.xlsx");
        byte[] bytes = Files.readAllBytes(f.toPath());
        MockMultipartFile file = new MockMultipartFile("Test",bytes);
        List<MemberContractClaimRequest> expectedResults =
                mockExcelService.extractPCPMemberClaimsData(file);
        Assertions.assertEquals(0, expectedResults.size());
    }

    @Test
    public void testExtractPCPMemberClaimsData_success() throws Exception{
        File f = new File("src/test/resources/data/ExcelServiceTestData.xlsx");
        byte[] bytes = Files.readAllBytes(f.toPath());
        MockMultipartFile file = new MockMultipartFile("Test",bytes);
        List<MemberContractClaimRequest> expectedResults =
                mockExcelService.extractPCPMemberClaimsData(file);
        Assertions.assertEquals(1, expectedResults.size());
    }

}
