package com.tms.boxes.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

//BoxEntity.java
@Entity
@Table(name = "boxes")
public class BoxEntity {
 @Id
 @Column(name = "box_id", length = 50)
 private String boxId;
 
 @Column(name = "box_name", nullable = false)
 private String boxName;
 
 @Column(name = "box_code", unique = true, nullable = false)
 private String boxCode;
 
 @Column(name = "hsn_code", nullable = false, length = 8)
 private String hsnCode;
 
 @Column(name = "description")
 private String description;
 
 @Column(name = "max_weight")
 private Double maxWeight;
 
 @Column(name = "dimensions") // Store as JSON: {"length": 10, "width": 20, "height": 30}
 private String dimensions;
 
 @Column(name = "total_value")
 private Double totalValue;
 
 @Column(name = "status")
 private String status; // EMPTY, PACKED, IN_TRANSIT, DELIVERED
 
 @Column(name = "branch_ids")
 private String branchIds;
 
 @Column(name = "created_by")
 private String createdBy;
 
 @Column(name = "created_date")
 private LocalDateTime createdDate;
 
 @Column(name = "last_modified_by")
 private String lastModifiedBy;
 
 @Column(name = "last_modified_date")
 private LocalDateTime lastModifiedDate;

public String getBoxId() {
	return boxId;
}

public void setBoxId(String boxId) {
	this.boxId = boxId;
}

public String getBoxName() {
	return boxName;
}

public void setBoxName(String boxName) {
	this.boxName = boxName;
}

public String getBoxCode() {
	return boxCode;
}

public void setBoxCode(String boxCode) {
	this.boxCode = boxCode;
}

public String getHsnCode() {
	return hsnCode;
}

public void setHsnCode(String hsnCode) {
	this.hsnCode = hsnCode;
}

public String getDescription() {
	return description;
}

public void setDescription(String description) {
	this.description = description;
}

public Double getMaxWeight() {
	return maxWeight;
}

public void setMaxWeight(Double maxWeight) {
	this.maxWeight = maxWeight;
}

public String getDimensions() {
	return dimensions;
}

public void setDimensions(String dimensions) {
	this.dimensions = dimensions;
}

public Double getTotalValue() {
	return totalValue;
}

public void setTotalValue(Double totalValue) {
	this.totalValue = totalValue;
}

public String getStatus() {
	return status;
}

public void setStatus(String status) {
	this.status = status;
}

public String getBranchIds() {
	return branchIds;
}

public void setBranchIds(String branchIds) {
	this.branchIds = branchIds;
}

public String getCreatedBy() {
	return createdBy;
}

public void setCreatedBy(String createdBy) {
	this.createdBy = createdBy;
}

public LocalDateTime getCreatedDate() {
	return createdDate;
}

public void setCreatedDate(LocalDateTime createdDate) {
	this.createdDate = createdDate;
}

public String getLastModifiedBy() {
	return lastModifiedBy;
}

public void setLastModifiedBy(String lastModifiedBy) {
	this.lastModifiedBy = lastModifiedBy;
}

public LocalDateTime getLastModifiedDate() {
	return lastModifiedDate;
}

public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
	this.lastModifiedDate = lastModifiedDate;
}
 


 
 
}
