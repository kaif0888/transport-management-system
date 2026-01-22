 package com.tms.bookingCost.bean;

import java.math.BigDecimal;
import java.time.LocalDate;

public class BookingCostBean   {
    
    private Long bookingCostId;
    private Long bookingId;
    private Long expenseTypeId;
    private BigDecimal amount;
    private String description;
    private LocalDate date;
    
	public Long getBookingCostId() {
		return bookingCostId;
	}
	public void setBookingCostId(Long bookingCostId) {
		this.bookingCostId = bookingCostId;
	}
	public Long getBookingId() {
		return bookingId;
	}
	public void setBookingId(Long bookingId) {
		this.bookingId = bookingId;
	}
	public Long getExpenseTypeId() {
		return expenseTypeId;
	}
	public void setExpenseTypeId(Long expenseTypeId) {
		this.expenseTypeId = expenseTypeId;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public LocalDate getDate() {
		return date;
	}
	public void setDate(LocalDate date) {
		this.date = date;
	}
    
    
	
 }
