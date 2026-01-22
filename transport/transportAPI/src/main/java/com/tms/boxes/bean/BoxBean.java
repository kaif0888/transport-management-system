package com.tms.boxes.bean;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class BoxBean {
    private String boxId;
    private String boxName;
    private String boxCode;
    private String hsnCode;
    private String hsnDescription;
    private String description;
    private Double maxWeight;
    private Map<String, Double> dimensions;
    private Double totalValue;
    private String status;
    private List<BoxProductBean> products;
    private String createdBy;
    private LocalDateTime createdDate;
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
	public String getHsnDescription() {
		return hsnDescription;
	}
	public void setHsnDescription(String hsnDescription) {
		this.hsnDescription = hsnDescription;
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
	public Map<String, Double> getDimensions() {
		return dimensions;
	}
	public void setDimensions(Map<String, Double> dimensions) {
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
	public List<BoxProductBean> getProducts() {
		return products;
	}
	public void setProducts(List<BoxProductBean> products) {
		this.products = products;
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
    
    
}
