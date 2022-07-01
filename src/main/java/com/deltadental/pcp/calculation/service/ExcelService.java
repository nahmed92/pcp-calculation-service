package com.deltadental.pcp.calculation.service;

import com.deltadental.pcp.calculation.domain.MemberContractClaimRequest;
import com.deltadental.platform.common.annotation.aop.MethodExecutionTime;
import com.deltadental.platform.common.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
@Slf4j
public class ExcelService {

    public static final String CONTENT_TYPE_EXCEL = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    public boolean isExcelFormat(MultipartFile file) {
        return CONTENT_TYPE_EXCEL.equals(file.getContentType());
    }

    @MethodExecutionTime
    public List<MemberContractClaimRequest> extractPCPMemberClaimsData(MultipartFile pcpMemberClaimsDataFile) {
        log.info("START ExcelService.extractPCPMemberClaimsData()");
        List<MemberContractClaimRequest> memberContractClaims = new ArrayList<>();
        try (InputStream is = pcpMemberClaimsDataFile.getInputStream(); Workbook workbook = new XSSFWorkbook(is)) {
            Sheet firstSheet = workbook.getSheetAt(0);
            Iterator<Row> rows = firstSheet.iterator();
            while (rows.hasNext()) {
                Row currentRow = rows.next();
                // skip header
                if (currentRow.getRowNum() == 0) {
                    continue; // just skip the rows if row number is 0
                }
                MemberContractClaimRequest memberContractClaim = new MemberContractClaimRequest();
                memberContractClaim.setOperatorId("FILE_UPLOAD");
                Iterator<Cell> cellsInRow = currentRow.iterator();
                int cellIdx = 0;
                while (cellsInRow.hasNext()) {
                    Cell currentCell = cellsInRow.next();
                    switch (cellIdx) {
                        case 0:
                            if (StringUtils.isNotBlank(currentCell.getStringCellValue())) {
                                memberContractClaim.setClaimId(currentCell.getStringCellValue().trim());
                            }
                            break;
                        case 1:
                            if (StringUtils.isNotBlank(currentCell.getStringCellValue())) {
                                memberContractClaim.setContractId(currentCell.getStringCellValue().trim());
                            }
                            break;
                        case 2:
                            if (StringUtils.isNotBlank(String.valueOf(currentCell.getStringCellValue()))) {
                                memberContractClaim.setMemberId(currentCell.getStringCellValue().trim());
                            }
                            break;
                        case 3:
                            if (StringUtils.isNotBlank(currentCell.getStringCellValue())) {
                                memberContractClaim.setProviderId(currentCell.getStringCellValue().trim());
                            }
                            break;
                        case 4:
                            if (StringUtils.isNotBlank(currentCell.getStringCellValue())) {
                                memberContractClaim.setState(currentCell.getStringCellValue().trim());
                            }
                            break;
                        default:
                            break;
                    }
                    cellIdx++;
                }
                memberContractClaims.add(memberContractClaim);
            }
        } catch (Exception e) {
            log.error("Unable to process to excel data ", e);
            throw new ServiceException("Fail to parse Excel file: " + e.getMessage());
        }
        log.info("END ExcelService.extractPCPMemberClaimsData()");
        return memberContractClaims;
    }
}