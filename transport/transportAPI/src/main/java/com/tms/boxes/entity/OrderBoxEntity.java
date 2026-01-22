package com.tms.boxes.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "order_boxes")
public class OrderBoxEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "order_id", nullable = false)
    private String orderId;
    
    @Column(name = "box_id", nullable = false)
    private String boxId;
    
    @Column(name = "added_date")
    private LocalDateTime addedDate;
    
    @Column(name = "box_sequence")
    private Integer boxSequence;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getBoxId() {
		return boxId;
	}

	public void setBoxId(String boxId) {
		this.boxId = boxId;
	}

	public LocalDateTime getAddedDate() {
		return addedDate;
	}

	public void setAddedDate(LocalDateTime addedDate) {
		this.addedDate = addedDate;
	}

	public Integer getBoxSequence() {
		return boxSequence;
	}

	public void setBoxSequence(Integer boxSequence) {
		this.boxSequence = boxSequence;
	}
    
    
    
}
