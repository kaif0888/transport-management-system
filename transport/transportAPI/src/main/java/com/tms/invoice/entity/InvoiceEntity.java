package com.tms.invoice.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.tms.customer.entity.CustomerEntity;
import com.tms.generic.entity.GenericEntity;
import com.tms.invoiceItem.entity.InvoiceItemEntity;
import com.tms.invoiceItem.entity.InvoiceStatus;
import com.tms.invoiceItem.entity.PaymentStatus;
import com.tms.location.entity.LocationEntity;
import com.tms.order.entity.OrderEntity;
import com.tms.payment.entity.PaymentEntity;

import jakarta.persistence.*;

@Entity
@Table(name = "invoice")
public class InvoiceEntity extends GenericEntity {

    @Id
    @Column(name = "invoiceId")
    private String invoiceId;

    @Column(name = "invoiceNumber", unique = true)
    private String invoiceNumber;
    
    @Column(name = "pdfPath")
    private String pdfPath;
    
    @OneToOne
    @JoinColumn(name="paymentId")
    private PaymentEntity payment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customerId")
    private CustomerEntity customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orderId")
    private OrderEntity order;
    
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="billingAddress")
    private LocationEntity billingAddress;
    
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="shippingAddress")
    private LocationEntity shippingAddress; 
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiverId")
    private CustomerEntity receiver;

    @Column(name = "invoiceDate")
    private LocalDateTime invoiceDate;

    @Column(name = "dueDate")
    private LocalDateTime dueDate;

    @Column(name = "subtotal", precision = 15, scale = 2)
    private BigDecimal subtotal;

    @Column(name = "taxAmount", precision = 15, scale = 2)
    private BigDecimal taxAmount;

    @Column(name = "taxPercentage", precision = 5, scale = 2)
    private BigDecimal taxPercentage;

    @Column(name = "discountAmount", precision = 15, scale = 2)
    private BigDecimal discountAmount;

    @Column(name = "discountPercentage", precision = 5, scale = 2)
    private BigDecimal discountPercentage;

    @Column(name = "totalAmount", precision = 15, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "paidAmount")
    private Double paidAmount;

    @Column(name = "balanceAmount", precision = 15, scale = 2)
    private BigDecimal balanceAmount;
 
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private InvoiceStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "paymentStatus")
    private PaymentStatus paymentStatus;
    
    

    @Column(name = "origin_location")
    private String originLocation;

    @Column(name = "destination_location")
    private String destinationLocation;
    
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "termsAndConditions", columnDefinition = "TEXT")
    private String termsAndConditions;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<InvoiceItemEntity> invoiceItems;

    // Constructors
    public InvoiceEntity() {}

    public InvoiceEntity(String invoiceId, String invoiceNumber, CustomerEntity customer, OrderEntity order) {
        this.invoiceId = invoiceId;
        this.invoiceNumber = invoiceNumber;
        this.customer = customer;
        this.order = order;
        this.invoiceDate = LocalDateTime.now();
        this.status = InvoiceStatus.DRAFT;
        this.paymentStatus = PaymentStatus.UNPAID;
    }

    // Getters and Setters
    public String getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public CustomerEntity getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerEntity customer) {
        this.customer = customer;
    }

    public OrderEntity getOrder() {
        return order;
    }

    public void setOrder(OrderEntity order) {
        this.order = order;
    }

    public LocalDateTime getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(LocalDateTime invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
    }

    public BigDecimal getTaxPercentage() {
        return taxPercentage;
    }

    public void setTaxPercentage(BigDecimal taxPercentage) {
        this.taxPercentage = taxPercentage;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public BigDecimal getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(BigDecimal discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Double getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(Double paidAmount) {
        this.paidAmount = paidAmount;
    }

    public BigDecimal getBalanceAmount() {
        return balanceAmount;
    }

    public void setBalanceAmount(BigDecimal balanceAmount) {
        this.balanceAmount = balanceAmount;
    }

    public InvoiceStatus getStatus() {
        return status;
    }

    public void setStatus(InvoiceStatus status) {
        this.status = status;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getTermsAndConditions() {
        return termsAndConditions;
    }

    public void setTermsAndConditions(String termsAndConditions) {
        this.termsAndConditions = termsAndConditions;
    }

    public List<InvoiceItemEntity> getInvoiceItems() {
        return invoiceItems;
    }

    public void setInvoiceItems(List<InvoiceItemEntity> invoiceItems) {
        this.invoiceItems = invoiceItems;
    }

	public String getPdfPath() {
		return pdfPath;
	}

	public void setPdfPath(String pdfPath) {
		this.pdfPath = pdfPath;
	}

	public LocationEntity getBillingAddress() {
		return billingAddress;
	}

	public void setBillingAddress(LocationEntity billingAddress) {
		this.billingAddress = billingAddress;
	}

	public LocationEntity getShippingAddress() {
		return shippingAddress;
	}

	public void setShippingAddress(LocationEntity shippingAddress) {
		this.shippingAddress = shippingAddress;
	}

	public PaymentEntity getPayment() {
		return payment;
	}

	public void setPayment(PaymentEntity payment) {
		this.payment = payment;
	}

	public CustomerEntity getReceiver() {
		return receiver;
	}

	public void setReceiver(CustomerEntity receiver) {
		this.receiver = receiver;
	}



	public String getOriginLocation() {
		return originLocation;
	}

	public void setOriginLocation(String originLocation) {
		this.originLocation = originLocation;
	}

	public String getDestinationLocation() {
		return destinationLocation;
	}

	public void setDestinationLocation(String destinationLocation) {
		this.destinationLocation = destinationLocation;
	}
    
	
   
}
