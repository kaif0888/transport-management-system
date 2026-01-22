package com.tms.boxes.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "box_products")
public class BoxProductEntity {
 @Id
 @GeneratedValue(strategy = GenerationType.IDENTITY)
 private Long id;
 
 @Column(name = "box_id", nullable = false)
 private String boxId;
 
 @Column(name = "product_id", nullable = false)
 private String productId;
 
 @Column(name = "quantity", nullable = false)
 private Integer quantity;
 
 @Column(name = "weight_per_unit")
 private Double weightPerUnit;
 
 @Column(name = "price_per_unit")
 private Double pricePerUnit;
 
 @Column(name = "total_weight")
 private Double totalWeight;
 
 @Column(name = "total_price")
 private Double totalPrice;
 
 @Column(name = "added_date")
 private LocalDateTime addedDate;

public Long getId() {
	return id;
}

public void setId(Long id) {
	this.id = id;
}

public String getBoxId() {
	return boxId;
}

public void setBoxId(String boxId) {
	this.boxId = boxId;
}

public String getProductId() {
	return productId;
}

public void setProductId(String productId) {
	this.productId = productId;
}

public Integer getQuantity() {
	return quantity;
}

public void setQuantity(Integer quantity) {
	this.quantity = quantity;
}

public Double getWeightPerUnit() {
	return weightPerUnit;
}

public void setWeightPerUnit(Double weightPerUnit) {
	this.weightPerUnit = weightPerUnit;
}

public Double getPricePerUnit() {
	return pricePerUnit;
}

public void setPricePerUnit(Double pricePerUnit) {
	this.pricePerUnit = pricePerUnit;
}

public Double getTotalWeight() {
	return totalWeight;
}

public void setTotalWeight(Double totalWeight) {
	this.totalWeight = totalWeight;
}

public Double getTotalPrice() {
	return totalPrice;
}

public void setTotalPrice(Double totalPrice) {
	this.totalPrice = totalPrice;
}

public LocalDateTime getAddedDate() {
	return addedDate;
}

public void setAddedDate(LocalDateTime addedDate) {
	this.addedDate = addedDate;
}
 
 
}
