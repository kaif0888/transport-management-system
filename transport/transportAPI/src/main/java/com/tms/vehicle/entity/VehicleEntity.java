package com.tms.vehicle.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.tms.generic.entity.GenericEntity;
import com.tms.vehicletype.entity.VehicleTypeEntity;

import jakarta.persistence.*;

@Entity
@Table(name = "vehicle")
public class VehicleEntity extends GenericEntity {

    /* =======================
       PRIMARY & BASIC FIELDS
       ======================= */

    @Id
    @Column(name = "vehicle_id")
    private String vehicleId;

    @Column(name = "branch_ids")
    private String branchIds;

    @Column(name = "registration_number")
    private String registrationNumber;

    @Column(name = "vehicl_number")
    private String vehiclNumber;

    @Column(name = "model")
    private String model;

    @Column(name = "capacity")
    private BigDecimal capacity;

    @Column(name = "is_rented")
    private Boolean isRented;

    @Column(name = "status")
    private String status;

    @Column(name = "owner_name")
    private String ownerName;

    /* =======================
       VEHICLE TYPE
       ======================= */

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_type_id")
    private VehicleTypeEntity vehicleType;

    /* =======================
       RENTAL DETAILS
       ======================= */

    @Column(name = "rental_vendor_name")
    private String rentalVendorName;

    @Column(name = "rental_start_date")
    private LocalDate rentalStartDate;

    @Column(name = "rental_end_date")
    private LocalDate rentalEndDate;

    @Column(name = "rental_amount_per_month")
    private BigDecimal rentalAmountPerMonth;

    @Column(name = "rental_agreement_number")
    private String rentalAgreementNumber;
    
    @Column(name = "rental_agreement_document_url")
    private String rentalAgreementDocumentUrl;

    /* =======================
       RC DETAILS
       ======================= */

    @Column(name = "rc_number")
    private String rcNumber;

    @Column(name = "rc_expiry_date")
    private LocalDate rcExpiryDate;

    @Column(name = "rc_document_url")
    private String rcDocumentUrl;

    /* =======================
       INSURANCE DETAILS
       ======================= */

    @Column(name = "insurance_policy_number")
    private String insurancePolicyNumber;

    @Column(name = "insurance_expiry_date")
    private LocalDate insuranceExpiryDate;

    @Column(name = "insurance_document_url")
    private String insuranceDocumentUrl;

    /* =======================
       FITNESS DETAILS
       ======================= */

    @Column(name = "fitness_certificate_number")
    private String fitnessCertificateNumber;

    @Column(name = "fitness_expiry_date")
    private LocalDate fitnessExpiryDate;

    @Column(name = "fitness_document_url")
    private String fitnessDocumentUrl;

    /* =======================
       PERMIT DETAILS
       ======================= */

    @Column(name = "permit_number")
    private String permitNumber;

    @Column(name = "permit_expiry_date")
    private LocalDate permitExpiryDate;

    @Column(name = "permit_document_url")
    private String permitDocumentUrl;

    /* =======================
       POLLUTION DETAILS
       ======================= */

    @Column(name = "pollution_certificate_number")
    private String pollutionCertificateNumber;

    @Column(name = "pollution_expiry_date")
    private LocalDate pollutionExpiryDate;

    @Column(name = "pollution_document_url")
    private String pollutionDocumentUrl;

    /* =======================
       TAX DETAILS
       ======================= */

    @Column(name = "tax_valid_upto")
    private LocalDate taxValidUpto;

    @Column(name = "road_tax_receipt_number")
    private String roadTaxReceiptNumber;

    @Column(name = "road_tax_document_url")
    private String roadTaxDocumentUrl;

    /* =======================
       CONSTRUCTORS
       ======================= */

    public VehicleEntity() {
        super();
    }

    public VehicleEntity(String vehicleId, String branchIds, String registrationNumber, String vehiclNumber,
            String model, BigDecimal capacity, Boolean isRented, String status, String ownerName,
            VehicleTypeEntity vehicleType, String rentalVendorName, LocalDate rentalStartDate, LocalDate rentalEndDate,
            BigDecimal rentalAmountPerMonth, String rentalAgreementNumber, String rentalAgreementDocumentUrl,
            String rcNumber, LocalDate rcExpiryDate, String rcDocumentUrl, String insurancePolicyNumber, 
            LocalDate insuranceExpiryDate, String insuranceDocumentUrl, String fitnessCertificateNumber, 
            LocalDate fitnessExpiryDate, String fitnessDocumentUrl, String permitNumber, LocalDate permitExpiryDate, 
            String permitDocumentUrl, String pollutionCertificateNumber, LocalDate pollutionExpiryDate, 
            String pollutionDocumentUrl, LocalDate taxValidUpto, String roadTaxReceiptNumber, String roadTaxDocumentUrl) {
        super();
        this.vehicleId = vehicleId;
        this.branchIds = branchIds;
        this.registrationNumber = registrationNumber;
        this.vehiclNumber = vehiclNumber;
        this.model = model;
        this.capacity = capacity;
        this.isRented = isRented;
        this.status = status;
        this.ownerName = ownerName;
        this.vehicleType = vehicleType;
        this.rentalVendorName = rentalVendorName;
        this.rentalStartDate = rentalStartDate;
        this.rentalEndDate = rentalEndDate;
        this.rentalAmountPerMonth = rentalAmountPerMonth;
        this.rentalAgreementNumber = rentalAgreementNumber;
        this.rentalAgreementDocumentUrl = rentalAgreementDocumentUrl;
        this.rcNumber = rcNumber;
        this.rcExpiryDate = rcExpiryDate;
        this.rcDocumentUrl = rcDocumentUrl;
        this.insurancePolicyNumber = insurancePolicyNumber;
        this.insuranceExpiryDate = insuranceExpiryDate;
        this.insuranceDocumentUrl = insuranceDocumentUrl;
        this.fitnessCertificateNumber = fitnessCertificateNumber;
        this.fitnessExpiryDate = fitnessExpiryDate;
        this.fitnessDocumentUrl = fitnessDocumentUrl;
        this.permitNumber = permitNumber;
        this.permitExpiryDate = permitExpiryDate;
        this.permitDocumentUrl = permitDocumentUrl;
        this.pollutionCertificateNumber = pollutionCertificateNumber;
        this.pollutionExpiryDate = pollutionExpiryDate;
        this.pollutionDocumentUrl = pollutionDocumentUrl;
        this.taxValidUpto = taxValidUpto;
        this.roadTaxReceiptNumber = roadTaxReceiptNumber;
        this.roadTaxDocumentUrl = roadTaxDocumentUrl;
    }

    /* =======================
       GETTERS AND SETTERS
       ======================= */

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getBranchIds() {
        return branchIds;
    }

    public void setBranchIds(String branchIds) {
        this.branchIds = branchIds;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public String getVehiclNumber() {
        return vehiclNumber;
    }

    public void setVehiclNumber(String vehiclNumber) {
        this.vehiclNumber = vehiclNumber;
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

    public VehicleTypeEntity getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(VehicleTypeEntity vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getRentalVendorName() {
        return rentalVendorName;
    }

    public void setRentalVendorName(String rentalVendorName) {
        this.rentalVendorName = rentalVendorName;
    }

    public LocalDate getRentalStartDate() {
        return rentalStartDate;
    }

    public void setRentalStartDate(LocalDate rentalStartDate) {
        this.rentalStartDate = rentalStartDate;
    }

    public LocalDate getRentalEndDate() {
        return rentalEndDate;
    }

    public void setRentalEndDate(LocalDate rentalEndDate) {
        this.rentalEndDate = rentalEndDate;
    }

    public BigDecimal getRentalAmountPerMonth() {
        return rentalAmountPerMonth;
    }

    public void setRentalAmountPerMonth(BigDecimal rentalAmountPerMonth) {
        this.rentalAmountPerMonth = rentalAmountPerMonth;
    }

    public String getRentalAgreementNumber() {
        return rentalAgreementNumber;
    }

    public void setRentalAgreementNumber(String rentalAgreementNumber) {
        this.rentalAgreementNumber = rentalAgreementNumber;
    }

    public String getRentalAgreementDocumentUrl() {
        return rentalAgreementDocumentUrl;
    }

    public void setRentalAgreementDocumentUrl(String rentalAgreementDocumentUrl) {
        this.rentalAgreementDocumentUrl = rentalAgreementDocumentUrl;
    }

    public String getRcNumber() {
        return rcNumber;
    }

    public void setRcNumber(String rcNumber) {
        this.rcNumber = rcNumber;
    }

    public LocalDate getRcExpiryDate() {
        return rcExpiryDate;
    }

    public void setRcExpiryDate(LocalDate rcExpiryDate) {
        this.rcExpiryDate = rcExpiryDate;
    }

    public String getRcDocumentUrl() {
        return rcDocumentUrl;
    }

    public void setRcDocumentUrl(String rcDocumentUrl) {
        this.rcDocumentUrl = rcDocumentUrl;
    }

    public String getInsurancePolicyNumber() {
        return insurancePolicyNumber;
    }

    public void setInsurancePolicyNumber(String insurancePolicyNumber) {
        this.insurancePolicyNumber = insurancePolicyNumber;
    }

    public LocalDate getInsuranceExpiryDate() {
        return insuranceExpiryDate;
    }

    public void setInsuranceExpiryDate(LocalDate insuranceExpiryDate) {
        this.insuranceExpiryDate = insuranceExpiryDate;
    }

    public String getInsuranceDocumentUrl() {
        return insuranceDocumentUrl;
    }

    public void setInsuranceDocumentUrl(String insuranceDocumentUrl) {
        this.insuranceDocumentUrl = insuranceDocumentUrl;
    }

    public String getFitnessCertificateNumber() {
        return fitnessCertificateNumber;
    }

    public void setFitnessCertificateNumber(String fitnessCertificateNumber) {
        this.fitnessCertificateNumber = fitnessCertificateNumber;
    }

    public LocalDate getFitnessExpiryDate() {
        return fitnessExpiryDate;
    }

    public void setFitnessExpiryDate(LocalDate fitnessExpiryDate) {
        this.fitnessExpiryDate = fitnessExpiryDate;
    }

    public String getFitnessDocumentUrl() {
        return fitnessDocumentUrl;
    }

    public void setFitnessDocumentUrl(String fitnessDocumentUrl) {
        this.fitnessDocumentUrl = fitnessDocumentUrl;
    }

    public String getPermitNumber() {
        return permitNumber;
    }

    public void setPermitNumber(String permitNumber) {
        this.permitNumber = permitNumber;
    }

    public LocalDate getPermitExpiryDate() {
        return permitExpiryDate;
    }

    public void setPermitExpiryDate(LocalDate permitExpiryDate) {
        this.permitExpiryDate = permitExpiryDate;
    }

    public String getPermitDocumentUrl() {
        return permitDocumentUrl;
    }

    public void setPermitDocumentUrl(String permitDocumentUrl) {
        this.permitDocumentUrl = permitDocumentUrl;
    }

    public String getPollutionCertificateNumber() {
        return pollutionCertificateNumber;
    }

    public void setPollutionCertificateNumber(String pollutionCertificateNumber) {
        this.pollutionCertificateNumber = pollutionCertificateNumber;
    }

    public LocalDate getPollutionExpiryDate() {
        return pollutionExpiryDate;
    }

    public void setPollutionExpiryDate(LocalDate pollutionExpiryDate) {
        this.pollutionExpiryDate = pollutionExpiryDate;
    }

    public String getPollutionDocumentUrl() {
        return pollutionDocumentUrl;
    }

    public void setPollutionDocumentUrl(String pollutionDocumentUrl) {
        this.pollutionDocumentUrl = pollutionDocumentUrl;
    }

    public LocalDate getTaxValidUpto() {
        return taxValidUpto;
    }

    public void setTaxValidUpto(LocalDate taxValidUpto) {
        this.taxValidUpto = taxValidUpto;
    }

    public String getRoadTaxReceiptNumber() {
        return roadTaxReceiptNumber;
    }

    public void setRoadTaxReceiptNumber(String roadTaxReceiptNumber) {
        this.roadTaxReceiptNumber = roadTaxReceiptNumber;
    }

    public String getRoadTaxDocumentUrl() {
        return roadTaxDocumentUrl;
    }

    public void setRoadTaxDocumentUrl(String roadTaxDocumentUrl) {
        this.roadTaxDocumentUrl = roadTaxDocumentUrl;
    }
}