package com.tms.order.bean;

import java.time.LocalDate;

import com.tms.generic.bean.GenericBean;

public class OrderBean extends GenericBean {

    private Long orderId;
    private Long customerId;
    private Long originlocationId;
    private Long destinationlocationId;
    private LocalDate dispatchDate;
    private LocalDate deliveryDate;
    private String status;
    private String paymentStatus;
	public Long getOrderId() {
		return orderId;
	}
	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}
	public Long getCustomerId() {
		return customerId;
	}
	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
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
