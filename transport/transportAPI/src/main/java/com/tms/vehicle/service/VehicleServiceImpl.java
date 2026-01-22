package com.tms.vehicle.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tms.JwtSecurity.entity.User;
import com.tms.JwtSecurity.repository.UserRepository;
import com.tms.filter.criteria.bean.FilterCriteriaBean;
import com.tms.filter.criteria.service.FilterCriteriaService;
import com.tms.util.FilePathUtil;
import com.tms.vehicle.bean.VehicleAvalaibleDropdown;
import com.tms.vehicle.bean.VehicleWithDocumentsRequest;
import com.tms.vehicle.bean.VehicleWithDocumentsRequest.DocumentUploadDTO;
import com.tms.vehicle.bean.VehicleWithDocumentsRequest.DocumentsDTO;
import com.tms.vehicle.bean.VehicleWithDocumentsRequest.RentalDTO;
import com.tms.vehicle.bean.VehicleWithDocumentsRequest.VehicleTypeDTO;
import com.tms.vehicle.entity.VehicleEntity;
import com.tms.vehicle.repository.VehicleRepository;
import com.tms.vehicletype.entity.VehicleTypeEntity;
import com.tms.vehicletype.repository.VehicleTypeRepository;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Service
public class VehicleServiceImpl implements VehicleService {

    @Value("${file.storage.path}")
    private String baseStoragePath;

    @Autowired
    private VehicleRepository vehicleRepository;
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VehicleTypeRepository vehicleTypeRepository;
    
    @Autowired
    private FilterCriteriaService<VehicleEntity> filterCriteriaService;

    /**
     * Generate unique vehicle ID with format: VEH-YYYYMMDD-XXX
     */
    private String generateUniqueVehicleId() {
        String prefix = "VEH-";
        String dateStr = java.time.LocalDate.now()
            .format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
        String fullPrefix = prefix + dateStr + "-";

        List<VehicleEntity> vehicles = vehicleRepository.findByVehicleIdStartingWith(fullPrefix);

        int maxSeq = vehicles.stream()
            .map(v -> v.getVehicleId().substring(fullPrefix.length()))
            .mapToInt(seq -> {
                try {
                    return Integer.parseInt(seq);
                } catch (NumberFormatException e) {
                    return 0;
                }
            })
            .max()
            .orElse(0);

        return fullPrefix + String.format("%03d", maxSeq + 1);
    }

    /**
     * Save file to disk and return the file URL
     * This is self-contained within the vehicle service
     */
    private String saveFileAndGetUrl(byte[] fileBytes, String fileName, String documentType) {
        try {
            // Create date-based path structure
            LocalDate today = LocalDate.now();
            String datePath = today.getYear() + File.separator + 
                            today.getMonthValue() + File.separator + 
                            today.getDayOfMonth();
            String fullPath = baseStoragePath + File.separator + "vehicles" + File.separator + datePath;

            // Create directory if not exists
            File dir = new File(fullPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // Create unique file name with timestamp
            String uniqueFileName = documentType + "_" + 
                                  System.currentTimeMillis() + "_" + 
                                  System.nanoTime() + "_" + 
                                  fileName;
            Path filePath = Paths.get(fullPath, uniqueFileName);

            // Write file to disk
            Files.write(filePath, fileBytes);

            // Return the file path as URL
            return FilePathUtil.toPublicUrl(filePath.toString());

        } catch (Exception e) {
            throw new RuntimeException("Failed to save file: " + e.getMessage(), e);
        }
    }

    /**
     * Save document from base64 and return file URL
     * No external dependencies - everything done here
     */
    private String saveDocumentAndGetUrl(DocumentUploadDTO docDTO) {
        try {
            String base64Data = docDTO.getFileBase64();
            if (base64Data == null || base64Data.isEmpty()) {
                throw new IllegalArgumentException("Base64 content is empty");
            }

            // Handle Data URI scheme (e.g., data:image/png;base64,...)
            if (base64Data.contains(",")) {
                base64Data = base64Data.split(",")[1];
            }
            
            // Decode base64 to bytes
            byte[] decodedBytes = Base64.getMimeDecoder().decode(base64Data);

            // Get file name
            String fileName = docDTO.getDocumentName() != null 
                ? docDTO.getDocumentName() 
                : "document_" + System.currentTimeMillis();

            // Get document type for file organization
            String documentType = docDTO.getDocumentType() != null 
                ? docDTO.getDocumentType() 
                : "GENERAL";

            // Save file and get URL
            return saveFileAndGetUrl(decodedBytes, fileName, documentType);

        } catch (Exception e) {
            throw new RuntimeException("Failed to save document: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public VehicleWithDocumentsRequest createVehicleWithDocuments(VehicleWithDocumentsRequest request) {
        // Validate registration number
        if (request.getRegistrationNumber() == null || request.getRegistrationNumber().isEmpty()) {
            throw new IllegalArgumentException("Registration number cannot be empty.");
        }

        // Check if vehicle already exists
        if (vehicleRepository.findByRegistrationNumberOrVehiclNumber(
                request.getRegistrationNumber(), 
                request.getVehicleNumber()).isPresent()) {
            throw new RuntimeException(
                "Vehicle with registration number '" + request.getRegistrationNumber() +
                "' or vehicle number '" + request.getVehicleNumber() + "' already exists."
            );
        }

        // Create vehicle entity
        VehicleEntity entity = new VehicleEntity();
        entity.setVehicleId(generateUniqueVehicleId());
        entity.setRegistrationNumber(request.getRegistrationNumber());
        entity.setVehiclNumber(request.getVehicleNumber());
        entity.setModel(request.getModel());
        entity.setCapacity(request.getCapacity());
        entity.setIsRented(request.getIsRented());
        entity.setStatus(request.getStatus());
        entity.setOwnerName(request.getOwnerName());
        entity.setCreatedDate(LocalDateTime.now());
        entity.setLastModifiedDate(LocalDateTime.now());

        // Set authentication details
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        entity.setCreatedBy(authentication.getName());

        User currentUser = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Logged-in user not found"));
        
        // Handle branchIds
        if (request.getBranchIds() != null && !request.getBranchIds().isEmpty()) {
            entity.setBranchIds(String.join(",", request.getBranchIds()));
        } else {
            entity.setBranchIds(currentUser.getBranchIds());
        }

        // Set vehicle type
        if (request.getVehicleType() != null && request.getVehicleType().getVehicleTypeId() != null) {
            VehicleTypeEntity vehicleType = vehicleTypeRepository
                .findById(request.getVehicleType().getVehicleTypeId())
                .orElseThrow(() -> new RuntimeException(
                    "Vehicle type not found with ID: " + request.getVehicleType().getVehicleTypeId()
                ));
            entity.setVehicleType(vehicleType);
        }

        // Handle rental details
        if (Boolean.TRUE.equals(request.getIsRented()) && request.getRental() != null) {
            RentalDTO rental = request.getRental();
            entity.setRentalVendorName(rental.getVendorName());
            entity.setRentalStartDate(rental.getStartDate());
            entity.setRentalEndDate(rental.getEndDate());
            entity.setRentalAmountPerMonth(rental.getAmountPerMonth());
            entity.setRentalAgreementNumber(rental.getAgreementNumber());

            // Save rental agreement document if provided
            if (rental.getDocument() != null && 
                rental.getDocument().getFileBase64() != null && 
                !rental.getDocument().getFileBase64().isEmpty()) {
                
                String fileUrl = saveDocumentAndGetUrl(rental.getDocument());
                entity.setRentalAgreementDocumentUrl(fileUrl);
            }
        }

        // Handle vehicle documents (RC, Insurance, Fitness, Permit, Pollution, Road Tax)
        if (request.getDocuments() != null) {
            DocumentsDTO docs = request.getDocuments();
            processDocumentToEntity(entity, docs.getRc(), "RC");
            processDocumentToEntity(entity, docs.getInsurance(), "Insurance");
            processDocumentToEntity(entity, docs.getFitness(), "Fitness");
            processDocumentToEntity(entity, docs.getPermit(), "Permit");
            processDocumentToEntity(entity, docs.getPollution(), "Pollution");
            processDocumentToEntity(entity, docs.getRoadTax(), "Road Tax");
        }

        // Save vehicle
        vehicleRepository.save(entity);

        return convertToRequest(entity);
    }

    @Override
    @Transactional
    public VehicleWithDocumentsRequest updateVehicle(String vehicleId, VehicleWithDocumentsRequest request) {
        // Find existing vehicle
        VehicleEntity vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new RuntimeException("Vehicle not found with ID: " + vehicleId));

        // Update basic fields
        if (request.getRegistrationNumber() != null) {
            if (request.getRegistrationNumber().isEmpty()) {
                throw new RuntimeException("Registration number cannot be empty");
            }
            vehicle.setRegistrationNumber(request.getRegistrationNumber());
        }

        if (request.getVehicleNumber() != null) {
            vehicle.setVehiclNumber(request.getVehicleNumber());
        }
        if (request.getModel() != null) {
            vehicle.setModel(request.getModel());
        }
        if (request.getCapacity() != null) {
            vehicle.setCapacity(request.getCapacity());
        }
        if (request.getStatus() != null) {
            vehicle.setStatus(request.getStatus());
        }
        if (request.getIsRented() != null) {
            vehicle.setIsRented(request.getIsRented());
        }
        if (request.getOwnerName() != null) {
            vehicle.setOwnerName(request.getOwnerName());
        }

        // Update branch IDs
        if (request.getBranchIds() != null && !request.getBranchIds().isEmpty()) {
            vehicle.setBranchIds(String.join(",", request.getBranchIds()));
        }

        // Update vehicle type
        if (request.getVehicleType() != null && 
            request.getVehicleType().getVehicleTypeId() != null) {
            VehicleTypeEntity vehicleType = vehicleTypeRepository
                .findById(request.getVehicleType().getVehicleTypeId())
                .orElseThrow(() -> new RuntimeException(
                    "Vehicle type not found with ID: " + request.getVehicleType().getVehicleTypeId()
                ));
            vehicle.setVehicleType(vehicleType);
        }

        // Handle rental details
        if (Boolean.TRUE.equals(request.getIsRented())) {
            RentalDTO rental = request.getRental();
            if (rental != null) {
                vehicle.setRentalVendorName(rental.getVendorName());
                vehicle.setRentalStartDate(rental.getStartDate());
                vehicle.setRentalEndDate(rental.getEndDate());
                vehicle.setRentalAmountPerMonth(rental.getAmountPerMonth());
                vehicle.setRentalAgreementNumber(rental.getAgreementNumber());

                // Handle rental document update
                if (rental.getDocument() != null && 
                    rental.getDocument().getFileBase64() != null && 
                    !rental.getDocument().getFileBase64().isEmpty()) {
                    
                    String fileUrl = saveDocumentAndGetUrl(rental.getDocument());
                    vehicle.setRentalAgreementDocumentUrl(fileUrl);
                }
            }
        } else {
            // Clear rental details if not rented
            vehicle.setRentalVendorName(null);
            vehicle.setRentalStartDate(null);
            vehicle.setRentalEndDate(null);
            vehicle.setRentalAmountPerMonth(null);
            vehicle.setRentalAgreementNumber(null);
            vehicle.setRentalAgreementDocumentUrl(null);
        }

        // Handle other documents
        if (request.getDocuments() != null) {
            DocumentsDTO docs = request.getDocuments();
            updateDocumentInEntity(vehicle, docs.getRc(), "RC");
            updateDocumentInEntity(vehicle, docs.getInsurance(), "Insurance");
            updateDocumentInEntity(vehicle, docs.getFitness(), "Fitness");
            updateDocumentInEntity(vehicle, docs.getPermit(), "Permit");
            updateDocumentInEntity(vehicle, docs.getPollution(), "Pollution");
            updateDocumentInEntity(vehicle, docs.getRoadTax(), "Road Tax");
        }

        // Update modification details
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        vehicle.setLastModifiedBy(authentication.getName());
        vehicle.setLastModifiedDate(LocalDateTime.now());

        vehicleRepository.save(vehicle);

        return convertToRequest(vehicle);
    }

    /**
     * Process and save document directly to vehicle entity
     */
    private void processDocumentToEntity(VehicleEntity vehicle, DocumentUploadDTO docDto, String docType) {
        if (docDto == null) return;

        // Update metadata
        updateDocumentMetadata(vehicle, docDto, docType);

        // Save file if base64 is provided
        if (docDto.getFileBase64() != null && !docDto.getFileBase64().isEmpty()) {
            String fileUrl = saveDocumentAndGetUrl(docDto);
            setDocumentUrl(vehicle, docType, fileUrl);
        }
    }

    /**
     * Update document in entity - handles both new upload and metadata update
     */
    private void updateDocumentInEntity(VehicleEntity vehicle, DocumentUploadDTO docDto, String docType) {
        if (docDto == null) return;

        // If new file is uploaded
        if (docDto.getFileBase64() != null && !docDto.getFileBase64().isEmpty()) {
            String fileUrl = saveDocumentAndGetUrl(docDto);
            setDocumentUrl(vehicle, docType, fileUrl);
        }

        // Always update metadata
        updateDocumentMetadata(vehicle, docDto, docType);
    }

    /**
     * Set document URL in vehicle entity based on type
     */
    private void setDocumentUrl(VehicleEntity vehicle, String docType, String fileUrl) {
        switch (docType) {
            case "RC":
                vehicle.setRcDocumentUrl(fileUrl);
                break;
            case "Insurance":
                vehicle.setInsuranceDocumentUrl(fileUrl);
                break;
            case "Fitness":
                vehicle.setFitnessDocumentUrl(fileUrl);
                break;
            case "Permit":
                vehicle.setPermitDocumentUrl(fileUrl);
                break;
            case "Pollution":
                vehicle.setPollutionDocumentUrl(fileUrl);
                break;
            case "Road Tax":
                vehicle.setRoadTaxDocumentUrl(fileUrl);
                break;
        }
    }

    /**
     * Update only document metadata without uploading new file
     */
    private void updateDocumentMetadata(VehicleEntity vehicle, DocumentUploadDTO docDto, String docType) {
        switch (docType) {
            case "RC":
                if (docDto.getNumber() != null) {
                    vehicle.setRcNumber(docDto.getNumber());
                }
                if (docDto.getExpiryDate() != null) {
                    vehicle.setRcExpiryDate(docDto.getExpiryDate());
                }
                break;
                
            case "Insurance":
                if (docDto.getNumber() != null) {
                    vehicle.setInsurancePolicyNumber(docDto.getNumber());
                }
                if (docDto.getExpiryDate() != null) {
                    vehicle.setInsuranceExpiryDate(docDto.getExpiryDate());
                }
                break;
                
            case "Fitness":
                if (docDto.getNumber() != null) {
                    vehicle.setFitnessCertificateNumber(docDto.getNumber());
                }
                if (docDto.getExpiryDate() != null) {
                    vehicle.setFitnessExpiryDate(docDto.getExpiryDate());
                }
                break;
                
            case "Permit":
                if (docDto.getNumber() != null) {
                    vehicle.setPermitNumber(docDto.getNumber());
                }
                if (docDto.getExpiryDate() != null) {
                    vehicle.setPermitExpiryDate(docDto.getExpiryDate());
                }
                break;
                
            case "Pollution":
                if (docDto.getNumber() != null) {
                    vehicle.setPollutionCertificateNumber(docDto.getNumber());
                }
                if (docDto.getExpiryDate() != null) {
                    vehicle.setPollutionExpiryDate(docDto.getExpiryDate());
                }
                break;
                
            case "Road Tax":
                if (docDto.getNumber() != null) {
                    vehicle.setRoadTaxReceiptNumber(docDto.getNumber());
                }
                if (docDto.getValidUpto() != null) {
                    vehicle.setTaxValidUpto(docDto.getValidUpto());
                }
                break;
        }
    }

    @Override
    public List<VehicleWithDocumentsRequest> getAllVehicles() {
        return vehicleRepository.findAll().stream()
            .map(this::convertToRequest)
            .collect(Collectors.toList());
    }

    @Override
    public List<VehicleWithDocumentsRequest> getVehiclesByRegistrationNumber(String registrationNumber) {
        List<VehicleEntity> vehicles = vehicleRepository
            .findByRegistrationNumberStartingWith(registrationNumber);
        return vehicles.stream()
            .map(this::convertToRequest)
            .collect(Collectors.toList());
    }

    @Override
    public Map<String, List<VehicleWithDocumentsRequest>> getVehiclesGroupedByModel() {
        List<VehicleEntity> allVehicles = vehicleRepository.findAll();
        return allVehicles.stream()
            .collect(Collectors.groupingBy(
                entity -> (entity.getModel() != null) ? entity.getModel() : "Unknown Model",
                Collectors.mapping(this::convertToRequest, Collectors.toList())
            ));
    }

    @Override
    @Transactional(readOnly = true)
    public VehicleWithDocumentsRequest getVehicleById(String vehicleId) {
        VehicleEntity entity = vehicleRepository.findByVehicleId(vehicleId)
            .orElseThrow(() -> new RuntimeException("Vehicle not found with ID: " + vehicleId));
        return convertToRequest(entity);
    }

    @Override
    public List<VehicleAvalaibleDropdown> getAvailableVehicles() {
        List<VehicleEntity> unassignedVehicles = vehicleRepository.findVehiclesNotAssignedToAnyDriver();
        return unassignedVehicles.stream()
            .map(entity -> {
                VehicleAvalaibleDropdown bean = new VehicleAvalaibleDropdown();
                bean.setVehicleId(entity.getVehicleId());
                bean.setRegistrationNumber(entity.getRegistrationNumber());
                bean.setVehiclNumber(entity.getVehiclNumber());
                bean.setModel(entity.getModel());
                return bean;
            })
            .collect(Collectors.toList());
    }

    @Override
    public List<VehicleAvalaibleDropdown> getUnrentedVehicles() {
        List<VehicleEntity> unrentedVehicles = vehicleRepository.findVehiclesNotRented();
        return unrentedVehicles.stream()
            .map(vehicle -> {
                VehicleAvalaibleDropdown bean = new VehicleAvalaibleDropdown();
                bean.setVehicleId(vehicle.getVehicleId());
                bean.setRegistrationNumber(vehicle.getRegistrationNumber());
                bean.setModel(vehicle.getModel());
                return bean;
            })
            .collect(Collectors.toList());
    }

    @Override
    public List<String> getDistinctVehicleModels() {
        List<VehicleEntity> allVehicles = vehicleRepository.findAll();
        Set<String> distinctModels = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        
        allVehicles.stream()
            .map(VehicleEntity::getModel)
            .filter(model -> model != null && !model.isEmpty())
            .forEach(distinctModels::add);
            
        return new ArrayList<>(distinctModels);
    }

    @Override
    public List<VehicleWithDocumentsRequest> getvehiclebyfilterCriteria(
            List<FilterCriteriaBean> filters, int limit) {
        try {
            List<?> filteredEntities = filterCriteriaService.getListOfFilteredData(
                VehicleEntity.class, filters, limit
            );
            
            return filteredEntities.stream()
                .map(entity -> convertToRequest((VehicleEntity) entity))
                .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error filtering Vehicle: " + e.getMessage());
        }
    }
    
    
    @Override
    public List<Map<String, Object>> getVehicleExpiryList() {

        List<VehicleEntity> vehicles = vehicleRepository.findAll();
        List<Map<String, Object>> expiryList = new ArrayList<>();

        LocalDate today = LocalDate.now();

        for (VehicleEntity v : vehicles) {

            Map<String, Object> data = new HashMap<>();

            // Fitness Expiry
            if (v.getFitnessExpiryDate() != null) {
                long daysLeft = ChronoUnit.DAYS.between(today, v.getFitnessExpiryDate());
                if (daysLeft <= 5) {
                    data.put("FitnessExpiry", v.getFitnessExpiryDate());
                }
            }

            // Insurance Expiry
            if (v.getInsuranceExpiryDate() != null) {
                long daysLeft = ChronoUnit.DAYS.between(today, v.getInsuranceExpiryDate());
                if (daysLeft <= 5) {
                    data.put("InsuranceExpiry", v.getInsuranceExpiryDate());
                }
            }

            // Permit Expiry
            if (v.getPermitExpiryDate() != null) {
                long daysLeft = ChronoUnit.DAYS.between(today, v.getPermitExpiryDate());
                if (daysLeft <= 5) {
                    data.put("PermitExpiry", v.getPermitExpiryDate());
                }
            }

            // Pollution Expiry
            if (v.getPollutionExpiryDate() != null) {
                long daysLeft = ChronoUnit.DAYS.between(today, v.getPollutionExpiryDate());
                if (daysLeft <= 5) {
                    data.put("PollutionExpiry", v.getPollutionExpiryDate());
                }
            }

            // RC Expiry
            if (v.getRcExpiryDate() != null) {
                long daysLeft = ChronoUnit.DAYS.between(today, v.getRcExpiryDate());
                if (daysLeft <= 5) {
                    data.put("RcExpiry", v.getRcExpiryDate());
                }
            }

            // Rental End Date
            if (v.getRentalEndDate() != null) {
                long daysLeft = ChronoUnit.DAYS.between(today, v.getRentalEndDate());
                if (daysLeft <= 5) {
                    data.put("RentalEndDate", v.getRentalEndDate());
                }
            }

            // Add only if at least one document is expired or expiring soon
            if (!data.isEmpty()) {
                data.put("RegistrationNumber", v.getRegistrationNumber());
                expiryList.add(data);
            }
        }

        return expiryList;
    }


    /**
     * Convert VehicleEntity to VehicleWithDocumentsRequest
     * All data comes from vehicle entity - no additional database calls
     */
    private VehicleWithDocumentsRequest convertToRequest(VehicleEntity entity) {
        VehicleWithDocumentsRequest request = new VehicleWithDocumentsRequest();
        
        // Basic fields
        request.setVehicleId(entity.getVehicleId());
        request.setRegistrationNumber(entity.getRegistrationNumber());
        request.setVehicleNumber(entity.getVehiclNumber());
        request.setModel(entity.getModel());
        request.setCapacity(entity.getCapacity());
        request.setIsRented(entity.getIsRented());
        request.setStatus(entity.getStatus());
        request.setOwnerName(entity.getOwnerName());
        
        // Convert branchIds
        if (entity.getBranchIds() != null && !entity.getBranchIds().isEmpty()) {
            request.setBranchIds(List.of(entity.getBranchIds().split(",")));
        }
        
        // Vehicle Type
        if (entity.getVehicleType() != null) {
            VehicleTypeDTO vehicleTypeDTO = new VehicleTypeDTO();
            vehicleTypeDTO.setVehicleTypeId(entity.getVehicleType().getVehicleTypeId());
            vehicleTypeDTO.setVehicleTypeName(entity.getVehicleType().getVehicleTypeName());
            request.setVehicleType(vehicleTypeDTO);
        }

        // Rental Details
        if (Boolean.TRUE.equals(entity.getIsRented())) {
            RentalDTO rentalDTO = new RentalDTO();
            rentalDTO.setVendorName(entity.getRentalVendorName());
            rentalDTO.setStartDate(entity.getRentalStartDate());
            rentalDTO.setEndDate(entity.getRentalEndDate());
            rentalDTO.setAmountPerMonth(entity.getRentalAmountPerMonth());
            rentalDTO.setAgreementNumber(entity.getRentalAgreementNumber());
            
            // Add rental document if exists
            if (entity.getRentalAgreementDocumentUrl() != null) {
                DocumentUploadDTO rentalDoc = new DocumentUploadDTO();
                rentalDoc.setFileUrl(FilePathUtil.toPublicUrl(entity.getRentalAgreementDocumentUrl()));
                rentalDoc.setDocumentType("RENTAL_AGREEMENT");
                rentalDoc.setDocumentName("Rental Agreement");
                rentalDTO.setDocument(rentalDoc);
            }
            
            request.setRental(rentalDTO);
        }
        
        // Build documents DTO from entity fields
        DocumentsDTO documentsDTO = new DocumentsDTO();
        
        // RC Document
        if (entity.getRcDocumentUrl() != null || entity.getRcNumber() != null) {
            DocumentUploadDTO rcDoc = new DocumentUploadDTO();
            rcDoc.setFileUrl(FilePathUtil.toPublicUrl(entity.getRcDocumentUrl()));
            rcDoc.setNumber(entity.getRcNumber());
            rcDoc.setExpiryDate(entity.getRcExpiryDate());
            rcDoc.setDocumentType("RC");
            rcDoc.setDocumentName("RC");
            documentsDTO.setRc(rcDoc);
        }
        
        // Insurance Document
        if (entity.getInsuranceDocumentUrl() != null || entity.getInsurancePolicyNumber() != null) {
            DocumentUploadDTO insuranceDoc = new DocumentUploadDTO();
            insuranceDoc.setFileUrl(FilePathUtil.toPublicUrl(entity.getInsuranceDocumentUrl()));
            insuranceDoc.setNumber(entity.getInsurancePolicyNumber());
            insuranceDoc.setExpiryDate(entity.getInsuranceExpiryDate());
            insuranceDoc.setDocumentType("Insurance");
            insuranceDoc.setDocumentName("Insurance");
            documentsDTO.setInsurance(insuranceDoc);
        }
        
        // Fitness Document
        if (entity.getFitnessDocumentUrl() != null || entity.getFitnessCertificateNumber() != null) {
            DocumentUploadDTO fitnessDoc = new DocumentUploadDTO();
            fitnessDoc.setFileUrl(FilePathUtil.toPublicUrl(entity.getFitnessDocumentUrl()));
            fitnessDoc.setNumber(entity.getFitnessCertificateNumber());
            fitnessDoc.setExpiryDate(entity.getFitnessExpiryDate());
            fitnessDoc.setDocumentType("Fitness");
            fitnessDoc.setDocumentName("Fitness");
            documentsDTO.setFitness(fitnessDoc);
        }
        
        // Permit Document
        if (entity.getPermitDocumentUrl() != null || entity.getPermitNumber() != null) {
            DocumentUploadDTO permitDoc = new DocumentUploadDTO();
            permitDoc.setFileUrl(FilePathUtil.toPublicUrl(entity.getPermitDocumentUrl()));
            permitDoc.setNumber(entity.getPermitNumber());
            permitDoc.setExpiryDate(entity.getPermitExpiryDate());
            permitDoc.setDocumentType("Permit");
            permitDoc.setDocumentName("Permit");
            documentsDTO.setPermit(permitDoc);
        }
        
        // Pollution Document
        if (entity.getPollutionDocumentUrl() != null || entity.getPollutionCertificateNumber() != null) {
            DocumentUploadDTO pollutionDoc = new DocumentUploadDTO();
            pollutionDoc.setFileUrl(FilePathUtil.toPublicUrl(entity.getPollutionDocumentUrl()));
            pollutionDoc.setNumber(entity.getPollutionCertificateNumber());
            pollutionDoc.setExpiryDate(entity.getPollutionExpiryDate());
            pollutionDoc.setDocumentType("Pollution");
            pollutionDoc.setDocumentName("Pollution");
            documentsDTO.setPollution(pollutionDoc);
        }
        
        // Road Tax Document
        if (entity.getRoadTaxDocumentUrl() != null || entity.getRoadTaxReceiptNumber() != null) {
            DocumentUploadDTO taxDoc = new DocumentUploadDTO();
            taxDoc.setFileUrl(FilePathUtil.toPublicUrl(entity.getRoadTaxDocumentUrl()));
            taxDoc.setNumber(entity.getRoadTaxReceiptNumber());
            taxDoc.setValidUpto(entity.getTaxValidUpto());
            taxDoc.setDocumentType("Road Tax");
            taxDoc.setDocumentName("Road Tax");
            documentsDTO.setRoadTax(taxDoc);
        }
        
        request.setDocuments(documentsDTO);

        return request;
    }
}