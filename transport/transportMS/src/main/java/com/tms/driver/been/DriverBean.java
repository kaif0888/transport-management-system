package com.tms.driver.been;


import java.time.LocalDate;
import java.util.Date;

import com.tms.vehicle.entity.VehicleEntity;

public class DriverBean {
    private Long driverId;
    private String name;
    private String licenseNumber;
    private LocalDate licenseExpiry;
    private String contactNumber;
    private Long assignedVehicleId;
    
    
	public Long getDriverId() {
		return driverId;
	}
	public void setDriverId(Long driverId) {
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
	public Long getAssignedVehicleId() {
		return assignedVehicleId;
	}
	public void setAssignedVehicleId(Long assignedVehicleId) {
		this.assignedVehicleId = assignedVehicleId;
	}
    
	}

 

