package com.tms.driver.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.List;

import com.tms.document.entity.DocumentEntity;
import com.tms.generic.entity.GenericEntity;
import com.tms.vehicle.entity.VehicleEntity;

@Entity
@Table(name = "driver")
public class DriverEntity extends GenericEntity{

    @Id
    @Column(name = "driver_id")
    private String driverId;

    @Column(name = "name")
    private String name;

    @Column(name = "licenseNumber")
    private String licenseNumber;

    @Column(name = "licenseExpiry")
    private LocalDate licenseExpiry;
    
    @Pattern(regexp = "^\\+91[0-9]{10}$", message = "Contact number must be in format +91XXXXXXXXXX")
    @Column(name = "contactNumber",length = 15)
    private String contactNumber;

    @ManyToOne
    @JoinColumn(name = "assignedVehicleId")
    private VehicleEntity assignedVehicle;
    
    @OneToMany(mappedBy = "driver", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DocumentEntity> documentIds;

	public String getDriverId() {
		return driverId;
	}

	public void setDriverId(String driverId) {
		this.driverId = driverId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLicenseNumber() {
		return licenseNumber;
	}

	public void setLicenseNumber(String licenseNumber) {
		this.licenseNumber = licenseNumber;
	}

	public LocalDate getLicenseExpiry() {
		return licenseExpiry;
	}

	public void setLicenseExpiry(LocalDate licenseExpiry) {
		this.licenseExpiry = licenseExpiry;
	}

	public String getContactNumber() {
		return contactNumber;
	}

	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}

	public VehicleEntity getAssignedVehicle() {
		return assignedVehicle;
	}

	public void setAssignedVehicle(VehicleEntity assignedVehicle) {
		this.assignedVehicle = assignedVehicle;
	}

	public List<DocumentEntity> getDocuments() {
		return documentIds;
	}

	public void setDocuments(List<DocumentEntity> documents) {
		this.documentIds = documents;
	}

    
    

	
    

}
