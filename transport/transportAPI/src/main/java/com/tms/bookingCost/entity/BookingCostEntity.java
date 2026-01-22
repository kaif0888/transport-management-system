package com.tms.bookingCost.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.tms.customer.entity.CustomerEntity;
import com.tms.expenseType.entity.ExpenseTypeEntity;
import com.tms.generic.entity.GenericEntity;
import com.tms.location.entity.LocationEntity;
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
public class BookingCostEntity extends GenericEntity {

    @Id
    @Column(name = "bookingCostId")
    private String bookingCostId;

    @ManyToOne
    @JoinColumn(name = "orderID")
    private OrderEntity order;

   
    
    @ManyToOne
    @JoinColumn(name = "customerId")
    private CustomerEntity customer;

    @Column(name = "amount")
    private BigDecimal amount;

    @ManyToOne
    @JoinColumn(name="origin")
    private LocationEntity origin;

    @ManyToOne
    @JoinColumn(name="destination")
    private LocationEntity destination;
    
    @Column(name = "bookingDate")
    private LocalDate bookingDate;
    
    @Column(name= "delveryDate")
    private LocalDate deliveryDate;

	public String getBookingCostId() {
		return bookingCostId;
	}

	public void setBookingCostId(String bookingCostId) {
		this.bookingCostId = bookingCostId;
	}



	public OrderEntity getOrder() {
		return order;
	}

	public void setOrder(OrderEntity order) {
		this.order = order;
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

	public CustomerEntity getCustomer() {
		return customer;
	}

	public void setCustomer(CustomerEntity customer) {
		this.customer = customer;
	}

	public LocationEntity getOrigin() {
		return origin;
	}

	public void setOrigin(LocationEntity origin) {
		this.origin = origin;
	}

	public LocationEntity getDestination() {
		return destination;
	}

	public void setDestination(LocationEntity destination) {
		this.destination = destination;
	}

	public LocalDate getDeliveryDate() {
		return deliveryDate;
	}

	public void setDeliveryDate(LocalDate deliveryDate) {
		this.deliveryDate = deliveryDate;
	}

	

	

	
    
}
