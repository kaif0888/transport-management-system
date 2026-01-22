package com.tms.document.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tms.driver.entity.DriverEntity;
import com.tms.expense.entity.ExpenseEntity;
import com.tms.generic.entity.GenericEntity;
import com.tms.vehicle.entity.VehicleEntity;

import jakarta.persistence.*;

@Entity
@Table(name = "documents")
public class DocumentEntity extends GenericEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "document_id", unique = true)
	private String documentId;

	@Column(name = "file_name")
	private String name;

	@Column(name = "file_type")
	private String type;

	@Column(name = "file_url")
	private String fileUrl;

	@Column(name = "document_name")
	private String documentName;

	@Column(name = "document_status")
	private String documentStatus;

	@ManyToOne
	@JoinColumn(name = "driver_id")
	@JsonIgnore
	private DriverEntity driver;

	@ManyToOne
	@JoinColumn(name = "vehicle_id")
	@JsonIgnore
	private VehicleEntity vehicle;

	@ManyToOne
	@JoinColumn(name = "expense_id")
	@JsonIgnore
	private ExpenseEntity expense;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDocumentId() {
		return documentId;
	}

	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getFileUrl() {
		return fileUrl;
	}

	public void setFileUrl(String fileUrl) {
		this.fileUrl = fileUrl;
	}

	public String getDocumentName() {
		return documentName;
	}

	public void setDocumentName(String documentName) {
		this.documentName = documentName;
	}

	public String getDocumentStatus() {
		return documentStatus;
	}

	public void setDocumentStatus(String documentStatus) {
		this.documentStatus = documentStatus;
	}

	public DriverEntity getDriver() {
		return driver;
	}

	public void setDriver(DriverEntity driver) {
		this.driver = driver;
	}

	public VehicleEntity getVehicle() {
		return vehicle;
	}

	public void setVehicle(VehicleEntity vehicle) {
		this.vehicle = vehicle;
	}

	public ExpenseEntity getExpense() {
		return expense;
	}

	public void setExpense(ExpenseEntity expense) {
		this.expense = expense;
	}

}
