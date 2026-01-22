package com.tms.expense.bean;



import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.tms.generic.bean.GenericBean;
import com.tms.vehicle.entity.VehicleEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;


public class ExpenseBean extends GenericBean {
    private String expenseId;
    private String vehicleId;
    private String vehiclNumber;
    private String expenseTypeId;
    private String expenseTypeName;
    private BigDecimal amount;
    private LocalDate date;
    private String description;
    private List<String> documentIds;
    
	public String getExpenseId() {
		return expenseId;
	}
	public void setExpenseId(String expenseId) {
		this.expenseId = expenseId;
	}
	public String getVehicleId() {
		return vehicleId;
	}
	public void setVehicleId(String vehicleId) {
		this.vehicleId = vehicleId;
	}
	public String getVehiclNumber() {
		return vehiclNumber;
	}
	public void setVehiclNumber(String vehiclNumber) {
		this.vehiclNumber = vehiclNumber;
	}
	public String getExpenseTypeId() {
		return expenseTypeId;
	}
	public void setExpenseTypeId(String expenseTypeId) {
		this.expenseTypeId = expenseTypeId;
	}
	public String getExpenseTypeName() {
		return expenseTypeName;
	}
	public void setExpenseTypeName(String expenseTypeName) {
		this.expenseTypeName = expenseTypeName;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	public LocalDate getDate() {
		return date;
	}
	public void setDate(LocalDate date) {
		this.date = date;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public List<String> getDocumentIds() {
		return documentIds;
	}
	public void setDocumentIds(List<String> documentIds) {
		this.documentIds = documentIds;
	}
    
  
}
