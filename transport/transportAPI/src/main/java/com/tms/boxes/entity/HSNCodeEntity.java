package com.tms.boxes.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "hsn_codes")
public class HSNCodeEntity {
    @Id
    @Column(name = "hsn_code", length = 8)
    private String hsnCode;
    
    @Column(name = "description", nullable = false)
    private String description;
    
    @Column(name = "gst_rate")
    private Double gstRate;
    
    @Column(name = "category")
    private String category;
    
    @Column(name = "sub_category")
    private String subCategory;
    
    @Column(name = "is_active")
    private Boolean isActive;

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

	public Double getGstRate() {
		return gstRate;
	}

	public void setGstRate(Double gstRate) {
		this.gstRate = gstRate;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getSubCategory() {
		return subCategory;
	}

	public void setSubCategory(String subCategory) {
		this.subCategory = subCategory;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}
    
    
    
}
