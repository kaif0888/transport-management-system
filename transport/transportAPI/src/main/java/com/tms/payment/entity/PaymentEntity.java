package com.tms.payment.entity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.tms.customer.entity.CustomerEntity;
import com.tms.generic.entity.GenericEntity;
import com.tms.invoice.entity.InvoiceEntity;
import com.tms.order.entity.OrderEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "PAYMENT_ENTITY")
public class PaymentEntity extends GenericEntity {

	@Id
	@Column(name = "paymentId", nullable = false)
	private String paymentId;

	@OneToOne
	@JoinColumn(name = "INVOICE_ID")
	private InvoiceEntity invoice;


	@Column(name = "paymentMethod")
	private String paymentMethod;

//    @Column(name = "amountPaid")
//    private BigDecimal amountPaid;

	@Column(name = "ADVANCE_PAYMENT")
	private Double advancePayment;

	@Column(name = "REMAINING_PAYMENT")
	private Double remainingPayment;

	@Column(name = "TOTAL_PAYMENT")
	private Double totalPayment;

	@Column(name = "paymentDate")
	private LocalDateTime paymentDate;

	@Column(name = "CUSTOMER_ID")
	private String customerId;

	@Column(name = "ORDER_ID")
	private String orderId;

	@OneToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "CUSTOMER_ID", nullable = false, updatable = false, insertable = false)
	@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "idCustomer")
	private CustomerEntity customerEntity;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ORDER_ID", insertable = false, updatable = false, nullable = false)
	private OrderEntity order;

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

	public LocalDateTime getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(LocalDateTime paymentDate) {
		this.paymentDate = paymentDate;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public CustomerEntity getCustomerEntity() {
		return customerEntity;
	}

	public void setCustomerEntity(CustomerEntity customerEntity) {
		this.customerEntity = customerEntity;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public OrderEntity getOrder() {
		return order;
	}

	public void setOrder(OrderEntity order) {
		this.order = order;
	}

}
