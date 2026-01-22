 package com.tms.bookingCost.bean;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.tms.generic.bean.GenericBean;

public class BookingCostBean extends GenericBean  {
    
    private String bookingCostId;
    private String orderId;
    private String customerId;
    private BigDecimal amount;
    private String originId;
    private String destinationId;
    private LocalDate bookingDate;
    private LocalDate deliveryDate;
    
	public String getBookingCostId() {
		return bookingCostId;
	}
	public void setBookingCostId(String bookingCostId) {
		this.bookingCostId = bookingCostId;
	}
	
	
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public LocalDate getBookingDate() {
		return bookingDate;
	}
	public void setBookingDate(LocalDate bookingDate) {
		this.bookingDate = bookingDate;
	}
	public String getCustomerId() {
		return customerId;
	}
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	
	public String getOriginId() {
		return originId;
	}
	public void setOriginId(String originId) {
		this.originId = originId;
	}
	public String getDestinationId() {
		return destinationId;
	}
	public void setDestinationId(String destinationId) {
		this.destinationId = destinationId;
	}
	public LocalDate getDeliveryDate() {
		return deliveryDate;
	}
	public void setDeliveryDate(LocalDate deliveryDate) {
		this.deliveryDate = deliveryDate;
	}
	
    
    
	
 }
