package com.tms.rental.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import com.tms.vehicle.entity.VehicleEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "RentalDetails")
public class RentalDetailsEntity {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rentalDetailsId")
    private Long rentalDetailsId;

    @Column(name = "providerName")
    private String providerName;

    @Column(name = "rentalStartDate")
    private LocalDate rentalStartDate;

    @Column(name = "rentalEndDate")
    private LocalDate rentalEndDate;

    @Column(name = "rentalCost")
    private BigDecimal rentalCost;

    @OneToOne
    @JoinColumn(name = "vehicleId")
    private VehicleEntity vehicle;

	public Long getRentalDetailsId() {
		return rentalDetailsId;
	}

	public void setRentalDetailsId(Long rentalDetailsId) {
		this.rentalDetailsId = rentalDetailsId;
	}

	public String getProviderName() {
		return providerName;
	}

	public void setProviderName(String providerName) {
		this.providerName = providerName;
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

	public BigDecimal getRentalCost() {
		return rentalCost;
	}

	public void setRentalCost(BigDecimal rentalCost) {
		this.rentalCost = rentalCost;
	}

	public VehicleEntity getVehicle() {
		return vehicle;
	}

	public void setVehicle(VehicleEntity vehicle) {
		this.vehicle = vehicle;
	}
    
    
}
