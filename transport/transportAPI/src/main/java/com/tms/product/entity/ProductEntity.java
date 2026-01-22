package com.tms.product.entity;

import java.math.BigDecimal;

import com.tms.generic.entity.GenericEntity;
import com.tms.productcategory.entity.ProductCategoryEntity;

import jakarta.persistence.*;

@Entity
@Table(name = "product")
public class ProductEntity extends GenericEntity{
	@Id
    @Column(name = "productId")
    private String productId;
    @Column(name = "boxCode", unique = true, nullable = false)
    private String boxCode;
    @Column(name = "boxName", nullable = false)
    private String boxName;
    @Column(name = "hsnCode", length = 8)
    private String hsnCode;
    @Column(name = "description")
    private String description;
    
    @Column(name = "Weight")
    private BigDecimal weight;
	
    @Column(name="height")
    private  Double height;
    
    @Column(name="width")
    private Double width;
    
    @Column(name="lenght")
    private Double length;
    
    @Column(name = "totalValue")
    private BigDecimal totalValue;
 
    @Column(name = "status")
    private String status; 
    // EMPTY, PACKED, IN_TRANSIT, DELIVERED
    @Column(name = "branchIds")
    private String branchIds;
    
    @Column(name="storageCondition")
    private String storageCondition;
    
   public String getProductId() {
		return productId;
	}
	public void setProductId(String productId) {
		this.productId = productId;
	}
	public String getBoxCode() {
		return boxCode;
	}
	public void setBoxCode(String boxCode) {
		this.boxCode = boxCode;
	}
	public String getBoxName() {
		return boxName;
	}
	public void setBoxName(String boxName) {
		this.boxName = boxName;
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
	public BigDecimal getWeight() {
		return weight;
	}
	public void setWeight(BigDecimal weight) {
		this.weight = weight;
	}
	public BigDecimal getTotalValue() {
		return totalValue;
	}
	public void setTotalValue(BigDecimal totalValue) {
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

	public Double getHeight() {
		return height;
	}
	public void setHeight(Double height) {
		this.height = height;
	}
	public Double getWidth() {
		return width;
	}
	public void setWidth(Double width) {
		this.width = width;
	}
	public Double getLength() {
		return length;
	}
	public void setLength(Double length) {
		this.length = length;
	}
	public String getStorageCondition() {
		return storageCondition;
	}
	public void setStorageCondition(String storageCondition) {
		this.storageCondition = storageCondition;
	}
    
    
    
}
