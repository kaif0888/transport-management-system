package com.tms.order.entity;

import java.time.LocalDate;
import java.util.List;

import com.tms.customer.entity.CustomerEntity;
import com.tms.generic.entity.GenericEntity;
import com.tms.location.entity.LocationEntity;

import com.tms.orderproduct.entity.OrderProductEntity;
import com.tms.product.entity.ProductEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "ORDER_ENTITY")
public class OrderEntity extends GenericEntity {

    @Id
    @Column(name = "ORDER_ID")
    private String orderId;
    
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderProductEntity> orderProducts;


    @ManyToOne
    @JoinColumn(name = "CUSTOMER_ID")
    private CustomerEntity customer;

    @ManyToOne
    @JoinColumn(name = "ORIGIN_LOCATION_ID")
    private LocationEntity originlocationId;

    @ManyToOne
    @JoinColumn(name = "DESTINATION_LOCATION_ID")
    private LocationEntity destinationlocationId;

    @OneToMany
    @JoinColumn(name = "ORDER_ID")
    private List<ProductEntity> productEntity;
    
    @Column(name = "DISPATCH_DATE")
    private LocalDate dispatchDate;

    @Column(name = "DELIVERY_DATE")
    private LocalDate deliveryDate;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "PAYMENT_STATUS")
    private String paymentStatus;

    @Column(name = "TOTAL_AMOUNT")
    private Double totalAmount;

    @ManyToOne
    @JoinColumn(name = "RECEIVER_ID")  
    private CustomerEntity receiver;

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public CustomerEntity getCustomer() {
		return customer;
	}

	public void setCustomer(CustomerEntity customer) {
		this.customer = customer;
	}

	public LocationEntity getOriginlocationId() {
		return originlocationId;
	}

	public void setOriginlocationId(LocationEntity originlocationId) {
		this.originlocationId = originlocationId;
	}

	public LocationEntity getDestinationlocationId() {
		return destinationlocationId;
	}

	public void setDestinationlocationId(LocationEntity destinationlocationId) {
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

	public Double getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(Double totalAmount) {
		this.totalAmount = totalAmount;
	}

	public CustomerEntity getReceiver() {
		return receiver;
	}

	public void setReceiver(CustomerEntity receiver) {
		this.receiver = receiver;
	}

	public List<OrderProductEntity> getOrderProducts() {
		return orderProducts;
	}

	public void setOrderProducts(List<OrderProductEntity> orderProducts) {
		this.orderProducts = orderProducts;
	}

	public List<ProductEntity> getProductEntity() {
		return productEntity;
	}

	public void setProductEntity(List<ProductEntity> productEntity) {
		this.productEntity = productEntity;
	}

    

    
}
