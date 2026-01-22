package com.tms.driver.been;


import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import com.tms.document.bean.DocumentBean;
import com.tms.generic.bean.GenericBean;
import com.tms.vehicle.entity.VehicleEntity;

public class DriverBean extends GenericBean{
    private String driverId;
    private String name;
    private String licenseNumber;
    private LocalDate licenseExpiry;
    private String contactNumber;
    private String assignedVehicleId;
    private String assignedVehicleNumber;
    private List<String> documentIds;
    

    
    
	

	public String getAssignedVehicleNumber() {
		return assignedVehicleNumber;
	}

	public void setAssignedVehicleNumber(String assignedVehicleNumber) {
		this.assignedVehicleNumber = assignedVehicleNumber;
	}

	public List<String> getDocumentIds() {
		return documentIds;
	}

	public void setDocumentIds(List<String> documentIds) {
		this.documentIds = documentIds;
	}


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
	public String getAssignedVehicleId() {
		return assignedVehicleId;
	}
	public void setAssignedVehicleId(String assignedVehicleId) {
		this.assignedVehicleId = assignedVehicleId;
	}
    
	}

 

