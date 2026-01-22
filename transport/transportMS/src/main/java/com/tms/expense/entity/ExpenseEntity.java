package com.tms.expense.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.tms.expenseType.entity.ExpenseTypeEntity;
import com.tms.vehicle.entity.VehicleEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="expense")
public class ExpenseEntity {
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "expenseId")
    private Long expenseId;

    @ManyToOne
    @JoinColumn(name = "vehicleId")
    private VehicleEntity vehicle;

    @ManyToOne
    @JoinColumn(name = "expenseTypeId")
    private ExpenseTypeEntity expenseType;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "description")
    private String description;

	public Long getExpenseId() {
		return expenseId;
	}

	public void setExpenseId(Long expenseId) {
		this.expenseId = expenseId;
	}

	public VehicleEntity getVehicle() {
		return vehicle;
	}

	public void setVehicle(VehicleEntity vehicle) {
		this.vehicle = vehicle;
	}

	public ExpenseTypeEntity getExpenseType() {
		return expenseType;
	}

	public void setExpenseType(ExpenseTypeEntity expenseType) {
		this.expenseType = expenseType;
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
    
    
	    
	   }
