package com.tms.driver.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.temporal.Temporal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.tms.vehicle.entity.VehicleEntity;

@Entity
@Table(name = "driver")
public class DriverEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "driverId")
    private Long driverId;

    @Column(name = "name")
    private String name;

    @Column(name = "licenseNumber")
    private String licenseNumber;

    @Column(name = "licenseExpiry")
    private LocalDate licenseExpiry;

    @Column(name = "contactNumber")
    private String contactNumber;

    @ManyToOne
    @JoinColumn(name = "assignedVehicleId")
    private VehicleEntity assignedVehicle;

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

	public VehicleEntity getAssignedVehicle() {
		return assignedVehicle;
	}

	public void setAssignedVehicle(VehicleEntity assignedVehicle) {
		this.assignedVehicle = assignedVehicle;
	}
    
    

}
