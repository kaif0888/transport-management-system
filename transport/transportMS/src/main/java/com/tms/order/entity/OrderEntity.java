package com.tms.order.entity;

import java.time.LocalDate;

import com.tms.customer.entity.CustomerEntity;
import com.tms.generic.entity.GenericEntity;
import com.tms.location.entity.LocationEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "ORDER_ENTITY")
public class OrderEntity extends GenericEntity {

	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    @Column(name = "orderId")
	    private Long orderId;

	    @ManyToOne
	    @JoinColumn(name = "customerId")
	    private CustomerEntity customer;

	    
	    @JoinColumn(name = "originlocationId")
	    private Long originlocationId;

	    
	    @JoinColumn(name = "destinationlocationId")
	    private Long destinationlocationId;

	    @Column(name = "dispatchDate")
	    private LocalDate dispatchDate;

	    @Column(name = "deliveryDate")
	    private LocalDate deliveryDate;

	    @Column(name = "status")
	    private String status;

	    @Column(name = "paymentStatus")
	    private String paymentStatus;

		public Long getOrderId() {
			return orderId;
		}

		public void setOrderId(Long orderId) {
			this.orderId = orderId;
		}

		public CustomerEntity getCustomer() {
			return customer;
		}

		public void setCustomer(CustomerEntity customer) {
			this.customer = customer;
		}

		public Long getOriginlocationId() {
			return originlocationId;
		}

		public void setOriginlocationId(Long originlocationId) {
			this.originlocationId = originlocationId;
		}

		public Long getDestinationlocationId() {
			return destinationlocationId;
		}

		public void setDestinationlocationId(Long destinationlocationId) {
			this.destinationlocationId = destinationlocationId;
		}

		public LocalDate getDispatchDate() {
			return dispatchDate;
		}

		public void setDispatchDate(LocalDate dispatchDate) {
			this.dispatchDate = dispatchDate;
		}

		public LocalDate getDeliveryDate() {
			return deliveryDate;
		}

		public void setDeliveryDate(LocalDate deliveryDate) {
			this.deliveryDate = deliveryDate;
		}

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

		public String getPaymentStatus() {
			return paymentStatus;
		}

		public void setPaymentStatus(String paymentStatus) {
			this.paymentStatus = paymentStatus;
		}

		
}
