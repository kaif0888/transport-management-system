package com.tms.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.tms.vehicle.entity.VehicleEntity;
import com.tms.vehicle.repository.VehicleRepository;
import com.tms.vehicletype.entity.VehicleTypeEntity;
import com.tms.vehicletype.repository.VehicleTypeRepository;

@Service
public class CSVService {

    @Autowired
    VehicleRepository vehicleRepository;

    @Autowired
    VehicleTypeRepository vehicleTypeRepository;

    public void saveVehicles(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        
        if (fileName == null) {
            throw new RuntimeException("File name is null");
        }
        
        // Determine file type and process accordingly
        if (fileName.endsWith(".csv")) {
            processCSVFile(file);
        } else if (fileName.endsWith(".xlsx") || fileName.endsWith(".xls")) {
            processExcelFile(file);
        } else {
            throw new RuntimeException("Unsupported file format. Please upload CSV or Excel file.");
        }
    }

    private void processCSVFile(MultipartFile file) {
        try (BufferedReader fileReader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), "UTF-8"));
             CSVParser csvParser = new CSVParser(fileReader,
                     CSVFormat.DEFAULT.withFirstRecordAsHeader()
                             .withIgnoreHeaderCase()
                             .withTrim()
                             .withIgnoreSurroundingSpaces())) {

            List<VehicleEntity> vehicles = new ArrayList<>();
            Iterable<CSVRecord> csvRecords = csvParser.getRecords();
            
            int rowNumber = 1; // Start from 1 (header is 0)

            for (CSVRecord csvRecord : csvRecords) {
                rowNumber++;
                try {
                    VehicleEntity vehicle = createVehicleFromRecord(csvRecord, rowNumber);
                    if (vehicle != null) {
                        vehicles.add(vehicle);
                    }
                } catch (Exception e) {
                    throw new RuntimeException("Error at row " + rowNumber + ": " + e.getMessage());
                }
            }

            if (vehicles.isEmpty()) {
                throw new RuntimeException("No valid vehicle records found in the file");
            }

            vehicleRepository.saveAll(vehicles);

        } catch (IOException e) {
            throw new RuntimeException("Failed to parse CSV file: " + e.getMessage());
        }
    }

    private void processExcelFile(MultipartFile file) {
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            List<VehicleEntity> vehicles = new ArrayList<>();

            // Get header row
            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                throw new RuntimeException("Excel file has no header row");
            }

            // Create header map
            List<String> headers = new ArrayList<>();
            for (Cell cell : headerRow) {
                String header = getCellValueAsString(cell).trim();
                headers.add(header);
            }

            // Process data rows
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                try {
                    VehicleEntity vehicle = createVehicleFromExcelRow(row, headers, i + 1);
                    if (vehicle != null) {
                        vehicles.add(vehicle);
                    }
                } catch (Exception e) {
                    throw new RuntimeException("Error at row " + (i + 1) + ": " + e.getMessage());
                }
            }

            if (vehicles.isEmpty()) {
                throw new RuntimeException("No valid vehicle records found in the Excel file");
            }

            vehicleRepository.saveAll(vehicles);

        } catch (IOException e) {
            throw new RuntimeException("Failed to parse Excel file: " + e.getMessage());
        }
    }

    private VehicleEntity createVehicleFromRecord(CSVRecord csvRecord, int rowNumber) {
        // Check if row is empty
        if (isEmptyRecord(csvRecord)) {
            return null;
        }

        VehicleEntity vehicle = new VehicleEntity();
        
        try {
            // Generate vehicle ID
            vehicle.setVehicleId("VEH" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());

            // Registration Number - REQUIRED
            String registrationNumber = getValueFromRecord(csvRecord, "registrationNumber");
            if (registrationNumber == null || registrationNumber.trim().isEmpty()) {
                throw new IllegalArgumentException("Registration number is required");
            }
            vehicle.setRegistrationNumber(registrationNumber.trim());

            // Vehicle Number - REQUIRED
            String vehicleNumber = getValueFromRecord(csvRecord, "vehiclNumber");
            if (vehicleNumber == null || vehicleNumber.trim().isEmpty()) {
                throw new IllegalArgumentException("Vehicle number is required");
            }
            vehicle.setVehiclNumber(vehicleNumber.trim());

            // Vehicle Type ID - REQUIRED
            String vehicleTypeId = getValueFromRecord(csvRecord, "vehicleTypeId");
            if (vehicleTypeId == null || vehicleTypeId.trim().isEmpty()) {
                throw new IllegalArgumentException("Vehicle type ID is required");
            }
            
            VehicleTypeEntity vehicleType = vehicleTypeRepository.findById(vehicleTypeId.trim())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Vehicle type not found with ID: " + vehicleTypeId));
            vehicle.setVehicleType(vehicleType);

            // Model - REQUIRED
            String model = getValueFromRecord(csvRecord, "model");
            if (model == null || model.trim().isEmpty()) {
                throw new IllegalArgumentException("Model is required");
            }
            vehicle.setModel(model.trim());

            // Capacity - REQUIRED
            String capacityStr = getValueFromRecord(csvRecord, "capacity");
            if (capacityStr == null || capacityStr.trim().isEmpty()) {
                throw new IllegalArgumentException("Capacity is required");
            }
            try {
                BigDecimal capacity = new BigDecimal(capacityStr.trim());
                vehicle.setCapacity(capacity);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid capacity value: " + capacityStr);
            }

            // Status - REQUIRED
            String status = getValueFromRecord(csvRecord, "status");
            if (status == null || status.trim().isEmpty()) {
                throw new IllegalArgumentException("Status is required");
            }
            vehicle.setStatus(status.trim());

            // Is Rented - Default to false
            vehicle.setIsRented(false);

            return vehicle;

        } catch (Exception e) {
            throw new RuntimeException("Row " + rowNumber + " - " + e.getMessage());
        }
    }

    private VehicleEntity createVehicleFromExcelRow(Row row, List<String> headers, int rowNumber) {
        // Check if row is empty
        if (isEmptyRow(row)) {
            return null;
        }

        VehicleEntity vehicle = new VehicleEntity();

        try {
            // Generate vehicle ID
            vehicle.setVehicleId("VEH" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());

            // Registration Number - REQUIRED
            String registrationNumber = getCellValue(row, headers, "registrationNumber");
            if (registrationNumber == null || registrationNumber.trim().isEmpty()) {
                throw new IllegalArgumentException("Registration number is required");
            }
            vehicle.setRegistrationNumber(registrationNumber.trim());

            // Vehicle Number - REQUIRED
            String vehicleNumber = getCellValue(row, headers, "vehiclNumber");
            if (vehicleNumber == null || vehicleNumber.trim().isEmpty()) {
                throw new IllegalArgumentException("Vehicle number is required");
            }
            vehicle.setVehiclNumber(vehicleNumber.trim());

            // Vehicle Type ID - REQUIRED
            String vehicleTypeId = getCellValue(row, headers, "vehicleTypeId");
            if (vehicleTypeId == null || vehicleTypeId.trim().isEmpty()) {
                throw new IllegalArgumentException("Vehicle type ID is required");
            }

            VehicleTypeEntity vehicleType = vehicleTypeRepository.findById(vehicleTypeId.trim())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Vehicle type not found with ID: " + vehicleTypeId));
            vehicle.setVehicleType(vehicleType);

            // Model - REQUIRED
            String model = getCellValue(row, headers, "model");
            if (model == null || model.trim().isEmpty()) {
                throw new IllegalArgumentException("Model is required");
            }
            vehicle.setModel(model.trim());

            // Capacity - REQUIRED
            String capacityStr = getCellValue(row, headers, "capacity");
            if (capacityStr == null || capacityStr.trim().isEmpty()) {
                throw new IllegalArgumentException("Capacity is required");
            }
            try {
                BigDecimal capacity = new BigDecimal(capacityStr.trim());
                vehicle.setCapacity(capacity);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid capacity value: " + capacityStr);
            }

            // Status - REQUIRED
            String status = getCellValue(row, headers, "status");
            if (status == null || status.trim().isEmpty()) {
                throw new IllegalArgumentException("Status is required");
            }
            vehicle.setStatus(status.trim());

            // Is Rented - Default to false
            vehicle.setIsRented(false);

            return vehicle;

        } catch (Exception e) {
            throw new RuntimeException("Row " + rowNumber + " - " + e.getMessage());
        }
    }

    private String getValueFromRecord(CSVRecord record, String columnName) {
        try {
            if (record.isMapped(columnName)) {
                String value = record.get(columnName);
                return (value != null && !value.trim().isEmpty()) ? value : null;
            }
            return null;
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private String getCellValue(Row row, List<String> headers, String columnName) {
        int columnIndex = -1;
        
        // Find column index (case-insensitive)
        for (int i = 0; i < headers.size(); i++) {
            if (headers.get(i).equalsIgnoreCase(columnName)) {
                columnIndex = i;
                break;
            }
        }

        if (columnIndex == -1) {
            return null;
        }

        Cell cell = row.getCell(columnIndex);
        if (cell == null) {
            return null;
        }

        return getCellValueAsString(cell);
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return null;
        }

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    // Handle numeric values
                    double numericValue = cell.getNumericCellValue();
                    // Check if it's a whole number
                    if (numericValue == Math.floor(numericValue)) {
                        return String.valueOf((long) numericValue);
                    } else {
                        return String.valueOf(numericValue);
                    }
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            case BLANK:
                return null;
            default:
                return null;
        }
    }

    private boolean isEmptyRecord(CSVRecord record) {
        for (String value : record) {
            if (value != null && !value.trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private boolean isEmptyRow(Row row) {
        if (row == null) {
            return true;
        }
        for (Cell cell : row) {
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                String value = getCellValueAsString(cell);
                if (value != null && !value.trim().isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }
}