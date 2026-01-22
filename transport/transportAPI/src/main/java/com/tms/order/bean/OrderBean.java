package com.tms.order.bean;

import java.time.LocalDate;

import com.tms.generic.bean.GenericBean;

public class OrderBean extends GenericBean {

	private String orderId;
	private String customerId;
	
	private String originlocationId;
	private String destinationlocationId;
	private LocalDate dispatchDate;
	private LocalDate deliveryDate;
	private String status;
	private String paymentStatus;
	private Double totalAmount;
	private String receiverId;
	private String receiverName;
	private String customerName;
//	private String locationName;
	private String originLocationName;
	private String destinationLocationName;

	


//	public String getLocationName() {
//		return locationName;
//	}
//
//	public void setLocationName(String locationName) {
//		this.locationName = locationName;
//	}

	public String getCustomerName() {
		return customerName;
	}

	public String getReceiverName() {
		return receiverName;
	}

	public void setReceiverName(String receiverName) {
		this.receiverName = receiverName;
	}

	public String getOriginLocationName() {
		return originLocationName;
	}

	public void setOriginLocationName(String originLocationName) {
		this.originLocationName = originLocationName;
	}

	public String getDestinationLocationName() {
		return destinationLocationName;
	}

	public void setDestinationLocationName(String destinationLocationName) {
		this.destinationLocationName = destinationLocationName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getReceiverId() {
		return receiverId;
	}

	public void setReceiverId(String receiverId) {
		this.receiverId = receiverId;
	}

	private Double advancePayment;
//	private Double totalPayment;
	private Double remainingPayment;

	public Double getAdvancePayment() {
		return advancePayment;
	}

	public void setAdvancePayment(Double advancePayment) {
		this.advancePayment = advancePayment;
	}

//	public Double getTotalPayment() {
//	    return totalPayment;
//	}
//
//	public void setTotalPayment(Double totalPayment) {
//	    this.totalPayment = totalPayment;
//	}

	public Double getRemainingPayment() {
		return remainingPayment;
	}

	public void setRemainingPayment(Double remainingPayment) {
		this.remainingPayment = remainingPayment;
	}

	public Double getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(Double totalAmount) {
		this.totalAmount = totalAmount;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getOriginlocationId() {
		return originlocationId;
	}

	public void setOriginlocationId(String originlocationId) {
		this.originlocationId = originlocationId;
	}

	public String getDestinationlocationId() {
		return destinationlocationId;
	}

	public void setDestinationlocationId(String destinationlocationId) {
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
