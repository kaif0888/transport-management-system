package com.tms.rental.bean;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

import com.tms.generic.bean.GenericBean;

public class RentalDetailsBean extends GenericBean{
    private String rentalDetailsId;
    private String providerName;
    private LocalDate rentalStartDate;
    private LocalDate rentalEndDate;
    private BigDecimal rentalCost;
    private String vehicleId;
    
    
	public String getRentalDetailsId() {
		return rentalDetailsId;
	}
	public void setRentalDetailsId(String rentalDetailsId) {
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
	public String getVehicleId() {
		return vehicleId;
	}
	public void setVehicleId(String vehicleId) {
		this.vehicleId = vehicleId;
	}
    
}
