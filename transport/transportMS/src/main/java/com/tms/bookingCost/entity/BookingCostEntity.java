package com.tms.bookingCost.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.tms.expenseType.entity.ExpenseTypeEntity;
import com.tms.order.entity.OrderEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "BOOKING_COST")
public class BookingCostEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bookingCostId")
    private Long bookingCostId;

    @ManyToOne
    @JoinColumn(name = "bookingId")
    private OrderEntity booking;

    @ManyToOne
    @JoinColumn(name = "expenseTypeId")
    private ExpenseTypeEntity expenseType;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "description")
    private String description;

    @Column(name = "date")
    private LocalDate date;

	public Long getBookingCostId() {
		return bookingCostId;
	}

	public void setBookingCostId(Long bookingCostId) {
		this.bookingCostId = bookingCostId;
	}

	public OrderEntity getBooking() {
		return booking;
	}

	public void setBooking(OrderEntity booking) {
		this.booking = booking;
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
