package com.tms.vehicle.entity;

import java.math.BigDecimal;

import com.tms.vehicletype.entity.VehicleTypeEntity;

import jakarta.persistence.*;

@Entity
@Table(name = "vehicle")
public class VehicleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vehicleId")
    private Long vehicleId;

    @Column(name = "registrationNumber")
    private String registrationNumber;

    @ManyToOne
    @JoinColumn(name = "vehicleTypeId")
    private VehicleTypeEntity vehicleType;

    @Column(name = "model")
    private String model;

    @Column(name = "capacity")
    private BigDecimal capacity;

    @Column(name = "isRented")
    private Boolean isRented;

    @Column(name = "status")
    private String status;

    @Column(name = "rentalDetailsId")
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

	public VehicleTypeEntity getVehicleType() {
		return vehicleType;
	}

	public void setVehicleType(VehicleTypeEntity vehicleType) {
		this.vehicleType = vehicleType;
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
