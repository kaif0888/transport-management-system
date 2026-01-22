package com.tms.payment.bean;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.tms.generic.bean.GenericBean;

public class PaymentBean extends GenericBean {

	private String paymentId;
//	private String invoiceId;
	private String paymentMethod;
//	private BigDecimal amountPaid;
	private LocalDateTime paymentDate;

	private Double advancePayment;
	private Double remainingPayment;
	private Double totalPayment;
	private String customerId;
	private String orderId;

	public String getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(String paymentId) {
		this.paymentId = paymentId;
	}

	public String getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public LocalDateTime getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(LocalDateTime paymentDate) {
		this.paymentDate = paymentDate;
	}

	public Double getAdvancePayment() {
		return advancePayment;
	}

	public void setAdvancePayment(Double advancePayment) {
		this.advancePayment = advancePayment;
	}

	public Double getRemainingPayment() {
		return remainingPayment;
	}

	public void setRemainingPayment(Double remainingPayment) {
		this.remainingPayment = remainingPayment;
	}

	public Double getTotalPayment() {
		return totalPayment;
	}

	public void setTotalPayment(Double totalPayment) {
		this.totalPayment = totalPayment;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

}
