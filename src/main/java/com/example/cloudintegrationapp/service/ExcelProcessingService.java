package com.example.cloudintegrationapp.service;

import com.example.cloudintegrationapp.integration.gcp.GcpService;
import com.example.cloudintegrationapp.model.CacheData;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.*;

@Service
public class ExcelProcessingService {
    
    private static final Logger logger = LoggerFactory.getLogger(ExcelProcessingService.class);
    
    @Autowired(required = false)
    private GcpService gcpService;
    
    @Autowired
    private RedisCacheService redisCacheService;
    
    @Autowired
    private ReferenceIdGenerator referenceIdGenerator;
    
    public Map<String, Object> parseExcelFromGcp(String filename) {
        logger.info("Starting Excel parsing for file: {}", filename);
        
        if (gcpService == null) {
            throw new RuntimeException("GCP service is not available");
        }
        
        try {
            // Download file from GCP
            byte[] fileData = gcpService.downloadObject(filename);
            logger.info("Downloaded file from GCP: {} ({} bytes)", filename, fileData.length);
            
            // Parse Excel file
            Map<String, Object> result = parseExcelFile(fileData, filename);
            
            // Cache each sheet to Redis with reference keys
            cacheExcelSheetsToRedis(result);
            
            logger.info("Successfully parsed and cached Excel file: {}", filename);
            return result;
            
        } catch (IOException e) {
            logger.error("Error downloading file from GCP: {}", filename, e);
            throw new RuntimeException("Failed to download file from GCP", e);
        } catch (Exception e) {
            logger.error("Error parsing Excel file: {}", filename, e);
            throw new RuntimeException("Failed to parse Excel file", e);
        }
    }
    
    private Map<String, Object> parseExcelFile(byte[] fileData, String filename) throws IOException {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> sheets = new ArrayList<>();
        
        try (ByteArrayInputStream bis = new ByteArrayInputStream(fileData);
             Workbook workbook = new XSSFWorkbook(bis)) {
            
            int numberOfSheets = workbook.getNumberOfSheets();
            logger.info("Excel file contains {} sheets", numberOfSheets);
            
            for (int i = 0; i < numberOfSheets; i++) {
                Sheet sheet = workbook.getSheetAt(i);
                String sheetName = sheet.getSheetName();
                
                Map<String, Object> sheetData = parseSheet(sheet, sheetName);
                sheets.add(sheetData);
            }
            
            result.put("filename", filename);
            result.put("totalSheets", numberOfSheets);
            result.put("sheets", sheets);
            
        }
        
        return result;
    }
    
    private Map<String, Object> parseSheet(Sheet sheet, String sheetName) {
        Map<String, Object> sheetData = new HashMap<>();
        sheetData.put("name", sheetName);
        
        List<String> headers = new ArrayList<>();
        List<Map<String, Object>> rows = new ArrayList<>();
        
        boolean firstRow = true;
        int rowCount = 0;
        
        for (Row row : sheet) {
            if (firstRow) {
                // Parse headers
                for (Cell cell : row) {
                    headers.add(getCellValue(cell));
                }
                firstRow = false;
            } else {
                // Parse data rows
                Map<String, Object> rowData = new LinkedHashMap<>();
                for (int i = 0; i < headers.size(); i++) {
                    Cell cell = row.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    rowData.put(headers.get(i), getCellValue(cell));
                }
                rows.add(rowData);
            }
            rowCount++;
        }
        
        sheetData.put("headers", headers);
        sheetData.put("rows", rows);
        sheetData.put("rowCount", rowCount - 1); // Exclude header row
        
        logger.info("Parsed sheet '{}': {} rows, {} columns", sheetName, rowCount - 1, headers.size());
        
        return sheetData;
    }
    
    private String getCellValue(Cell cell) {
        if (cell == null) {
            return "";
        }
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    // Format numeric values without decimals if they are whole numbers
                    double numericValue = cell.getNumericCellValue();
                    if (numericValue == (long) numericValue) {
                        return String.valueOf((long) numericValue);
                    } else {
                        return String.valueOf(numericValue);
                    }
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return getCellValue(cell);
            default:
                return "";
        }
    }
    
    private void cacheExcelSheetsToRedis(Map<String, Object> excelData) {
        String filename = (String) excelData.get("filename");
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> sheets = (List<Map<String, Object>>) excelData.get("sheets");
        
        List<Map<String, String>> cachedSheets = new ArrayList<>();
        
        for (Map<String, Object> sheet : sheets) {
            String sheetName = (String) sheet.get("name");
            String referenceId = referenceIdGenerator.generateReferenceId("EXCEL");
            
            // Create CacheData object with sheet data
            Map<String, Object> sheetContent = new HashMap<>();
            sheetContent.put("filename", filename);
            sheetContent.put("sheetName", sheetName);
            sheetContent.put("headers", sheet.get("headers"));
            sheetContent.put("rows", sheet.get("rows"));
            sheetContent.put("rowCount", sheet.get("rowCount"));
            
            CacheData cacheData = new CacheData(referenceId, "EXCEL_SHEET", sheetContent, 3600L);
            redisCacheService.storeData(referenceId, cacheData);
            
            Map<String, String> cachedInfo = new HashMap<>();
            cachedInfo.put("sheetName", sheetName);
            cachedInfo.put("referenceId", referenceId);
            cachedInfo.put("rowCount", String.valueOf(sheet.get("rowCount")));
            cachedSheets.add(cachedInfo);
            
            logger.info("Cached sheet '{}' to Redis with reference ID: {}", sheetName, referenceId);
        }
        
        excelData.put("cachedSheets", cachedSheets);
    }
}

