package com.tms.expense.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.tms.document.entity.DocumentEntity;
import com.tms.expenseType.entity.ExpenseTypeEntity;
import com.tms.generic.entity.GenericEntity;
import com.tms.vehicle.entity.VehicleEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "expense")
public class ExpenseEntity extends GenericEntity {

	@Id
	@Column(name = "expenseId")
	private String expenseId;

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

	@OneToMany(mappedBy = "expense", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<DocumentEntity> documentIds;

	public String getExpenseId() {
		return expenseId;
	}

	public void setExpenseId(String expenseId) {
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

	public List<DocumentEntity> getDocumentIds() {
		return documentIds;
	}

	public void setDocumentIds(List<DocumentEntity> documentIds) {
		this.documentIds = documentIds;
	}

}
