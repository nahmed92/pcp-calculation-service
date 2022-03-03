package com.deltadental.pcp.calculation.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import com.deltadental.pcp.calculation.domain.ValidateProviderRequest;

public class ExcelHelper {

	public static String TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
	
	public static boolean hasExcelFormat(MultipartFile file) {
		if (!TYPE.equals(file.getContentType())) {
			return false;
		}
		return true;
	}

	public static List<ValidateProviderRequest> extractPCPMemberClaimsData(MultipartFile pcpMemberClaimsDataFile) {
		List<ValidateProviderRequest> validateProviderRequests = new ArrayList<ValidateProviderRequest>();
		try {
			InputStream is = pcpMemberClaimsDataFile.getInputStream();
			Workbook workbook = new XSSFWorkbook(is);
			Sheet firstSheet = (Sheet) workbook.getSheetAt(0);
			Iterator<Row> rows = firstSheet.iterator();
			while (rows.hasNext()) {
				Row currentRow = rows.next();
				// skip header
				if (currentRow.getRowNum() == 0) {
					continue; // just skip the rows if row number is 0 or 1
				}
				ValidateProviderRequest validateProviderRequest = new ValidateProviderRequest();
				Iterator<Cell> cellsInRow = currentRow.iterator();
				int cellIdx = 0;
				while (cellsInRow.hasNext()) {
					Cell currentCell = cellsInRow.next();
					switch (cellIdx) {
					case 0:
						if (StringUtils.isNotBlank(currentCell.getStringCellValue())) {
							validateProviderRequest.setClaimId(currentCell.getStringCellValue().trim());
						}
						break;
					case 1:
						if (StringUtils.isNotBlank(currentCell.getStringCellValue())) {
							validateProviderRequest.setContractId(currentCell.getStringCellValue().trim());
						}
						break;
					case 2:
						if (StringUtils.isNotBlank(String.valueOf(currentCell.getStringCellValue()))) {
							validateProviderRequest.setMemberId(currentCell.getStringCellValue().trim());
						}
						break;
					case 3:
						if (StringUtils.isNotBlank(currentCell.getStringCellValue())) {
							validateProviderRequest.setProviderId(currentCell.getStringCellValue().trim());
						}
						break;
					case 4:
						if (StringUtils.isNotBlank(currentCell.getStringCellValue())) {
							validateProviderRequest.setState(currentCell.getStringCellValue().trim());
						}
						break;
					default:
						break;
					}
					cellIdx++;
				}
				validateProviderRequests.add(validateProviderRequest);
			}
			workbook.close();
		} catch (IOException e) {
			throw new RuntimeException("fail to parse Excel file: " + e.getMessage());
		}
		return validateProviderRequests;
	}
}
