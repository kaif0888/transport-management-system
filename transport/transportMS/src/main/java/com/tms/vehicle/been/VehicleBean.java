package com.tms.vehicle.been;

import java.math.BigDecimal;

public class VehicleBean {
    private Long vehicleId;
    private String registrationNumber;
    private Long vehicleTypeId;
    private String model;
    private BigDecimal capacity;
    private Boolean isRented;
    private String status;
    private Long rentalDetailsId;
	public Long getVehicleId() {
		return vehicleId;
	}
	public void setVehicleId(Long vehicleId) {
		this.vehicleId = vehicleId;
	}
	public String getRegistrationNumber() {
		return registrationNumber;
	}
	public void setRegistrationNumber(String registrationNumber) {
		this.registrationNumber = registrationNumber;
	}
	public Long getVehicleTypeId() {
		return vehicleTypeId;
	}
	public void setVehicleTypeId(Long vehicleTypeId) {
		this.vehicleTypeId = vehicleTypeId;
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
	public Long getRentalDetailsId() {
		return rentalDetailsId;
	}
	public void setRentalDetailsId(Long rentalDetailsId) {
		this.rentalDetailsId = rentalDetailsId;
	}
    
    
}

