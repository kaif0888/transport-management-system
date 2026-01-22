package com.tms.vehicle.bean;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import com.tms.document.entity.DocumentEntity;

public class VehicleWithDocumentsRequest {

    // Vehicle basic details
    private String vehicleId;
    private List<String> branchIds;
    private String registrationNumber;
    private String vehicleNumber;
    private String model;
    private BigDecimal capacity;
    private Boolean isRented;
    private String status;
    private String ownerName;
    private LocalDateTime lastModifiedDate;
    private String lastModifiedBy;

    // Vehicle type
    private VehicleTypeDTO vehicleType;

    // Rental details (if applicable)
    private RentalDTO rental;

    // Documents
    private DocumentsDTO documents;

    private List<DocumentEntity> vehicleDocuments; // Add this field

    public List<DocumentEntity> getVehicleDocuments() {
        return vehicleDocuments;
    }

    public void setVehicleDocuments(List<DocumentEntity> vehicleDocuments) {
        this.vehicleDocuments = vehicleDocuments;
    }

    // Nested DTOs
    public static class VehicleTypeDTO {
        private String vehicleTypeId;
        private String vehicleTypeName;

        public String getVehicleTypeId() {
            return vehicleTypeId;
        }

        public void setVehicleTypeId(String vehicleTypeId) {
            this.vehicleTypeId = vehicleTypeId;
        }

        public String getVehicleTypeName() {
            return vehicleTypeName;
        }

        public void setVehicleTypeName(String vehicleTypeName) {
            this.vehicleTypeName = vehicleTypeName;
        }
    }

    public static class RentalDTO {
        private String vendorName;
        private LocalDate startDate;
        private LocalDate endDate;
        private BigDecimal amountPerMonth;
        private String agreementNumber;
        private DocumentUploadDTO document;

        public String getVendorName() {
            return vendorName;
        }

        public void setVendorName(String vendorName) {
            this.vendorName = vendorName;
        }

        public LocalDate getStartDate() {
            return startDate;
        }

        public void setStartDate(LocalDate startDate) {
            this.startDate = startDate;
        }

        public LocalDate getEndDate() {
            return endDate;
        }

        public void setEndDate(LocalDate endDate) {
            this.endDate = endDate;
        }

        public BigDecimal getAmountPerMonth() {
            return amountPerMonth;
        }

        public void setAmountPerMonth(BigDecimal amountPerMonth) {
            this.amountPerMonth = amountPerMonth;
        }

        public String getAgreementNumber() {
            return agreementNumber;
        }

        public void setAgreementNumber(String agreementNumber) {
            this.agreementNumber = agreementNumber;
        }

        public DocumentUploadDTO getDocument() {
            return document;
        }

        public void setDocument(DocumentUploadDTO document) {
            this.document = document;
        }
    }

    public static class DocumentsDTO {
        private DocumentUploadDTO rc;
        private DocumentUploadDTO insurance;
        private DocumentUploadDTO fitness;
        private DocumentUploadDTO permit;
        private DocumentUploadDTO pollution;
        private DocumentUploadDTO roadTax;

        public DocumentUploadDTO getRc() {
            return rc;
        }

        public void setRc(DocumentUploadDTO rc) {
            this.rc = rc;
        }

        public DocumentUploadDTO getInsurance() {
            return insurance;
        }

        public void setInsurance(DocumentUploadDTO insurance) {
            this.insurance = insurance;
        }

        public DocumentUploadDTO getFitness() {
            return fitness;
        }

        public void setFitness(DocumentUploadDTO fitness) {
            this.fitness = fitness;
        }

        public DocumentUploadDTO getPermit() {
            return permit;
        }

        public void setPermit(DocumentUploadDTO permit) {
            this.permit = permit;
        }

        public DocumentUploadDTO getPollution() {
            return pollution;
        }

        public void setPollution(DocumentUploadDTO pollution) {
            this.pollution = pollution;
        }

        public DocumentUploadDTO getRoadTax() {
            return roadTax;
        }

        public void setRoadTax(DocumentUploadDTO roadTax) {
            this.roadTax = roadTax;
        }
    }

    public static class DocumentUploadDTO {
        private String documentId; // Add this field
        private String documentType;
        private String documentName;
        private String contentType;
        private String fileBase64;
        private String documentStatus;
        private String fileUrl;
        
        // Document-specific fields
        private String number;
        private String policyNumber;
        private String certificateNumber;
        private String permitNumber;
        private String receiptNumber;
        private LocalDate expiryDate;
        private LocalDate validUpto;

        public String getDocumentId() {
            return documentId;
        }

        public void setDocumentId(String documentId) {
            this.documentId = documentId;
        }

        public String getFileUrl() {
            return fileUrl;
        }

        public void setFileUrl(String fileUrl) {
            this.fileUrl = fileUrl;
        }

        public String getDocumentType() {
            return documentType;
        }

        public void setDocumentType(String documentType) {
            this.documentType = documentType;
        }

        public String getDocumentName() {
            return documentName;
        }

        public void setDocumentName(String documentName) {
            this.documentName = documentName;
        }

        public String getContentType() {
            return contentType;
        }

        public void setContentType(String contentType) {
            this.contentType = contentType;
        }

        public String getFileBase64() {
            return fileBase64;
        }

        public void setFileBase64(String fileBase64) {
            this.fileBase64 = fileBase64;
        }

        public String getDocumentStatus() {
            return documentStatus;
        }

        public void setDocumentStatus(String documentStatus) {
            this.documentStatus = documentStatus;
        }

        public String getNumber() {
            return number;
        }

        public void setNumber(String number) {
            this.number = number;
        }

        public String getPolicyNumber() {
            return policyNumber;
        }

        public void setPolicyNumber(String policyNumber) {
            this.policyNumber = policyNumber;
        }

        public String getCertificateNumber() {
            return certificateNumber;
        }

        public void setCertificateNumber(String certificateNumber) {
            this.certificateNumber = certificateNumber;
        }

        public String getPermitNumber() {
            return permitNumber;
        }

        public void setPermitNumber(String permitNumber) {
            this.permitNumber = permitNumber;
        }

        public String getReceiptNumber() {
            return receiptNumber;
        }

        public void setReceiptNumber(String receiptNumber) {
            this.receiptNumber = receiptNumber;
        }

        public LocalDate getExpiryDate() {
            return expiryDate;
        }

        public void setExpiryDate(LocalDate expiryDate) {
            this.expiryDate = expiryDate;
        }

        public LocalDate getValidUpto() {
            return validUpto;
        }

        public void setValidUpto(LocalDate validUpto) {
            this.validUpto = validUpto;
        }
    }

    // Getters and Setters for main class
    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public List<String> getBranchIds() {
        return branchIds;
    }

    public void setBranchIds(List<String> branchIds) {
        this.branchIds = branchIds;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public BigDecimal getCapacity() {
        return capacity;
    }

    public void setCapacity(BigDecimal capacity) {
        this.capacity = capacity;
    }

    public Boolean getIsRented() {
        return isRented;
    }

    public void setIsRented(Boolean isRented) {
        this.isRented = isRented;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public VehicleTypeDTO getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(VehicleTypeDTO vehicleType) {
        this.vehicleType = vehicleType;
    }

    public RentalDTO getRental() {
        return rental;
    }

    public void setRental(RentalDTO rental) {
        this.rental = rental;
    }

    public DocumentsDTO getDocuments() {
        return documents;
    }

    public void setDocuments(DocumentsDTO documents) {
        this.documents = documents;
    }
}