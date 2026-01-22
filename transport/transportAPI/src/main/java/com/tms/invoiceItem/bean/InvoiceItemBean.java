package com.tms.invoiceItem.bean;

import java.math.BigDecimal;

public class InvoiceItemBean {

    private String invoiceItemId;
    private String invoiceId;
    private String productId;
    private String productName;
    private String description;
    private BigDecimal quantity;
    private BigDecimal unitPrice;
 
    private BigDecimal weight;
    private BigDecimal totalPrice;
    private BigDecimal taxAmount;
    private BigDecimal discountAmount;

    // Constructors
    public InvoiceItemBean() {}

    // Getters and Setters
    public String getInvoiceItemId() {
        return invoiceItemId;
    }

    public void setInvoiceItemId(String invoiceItemId) {
        this.invoiceItemId = invoiceItemId;
    }

    public String getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

  

    public BigDecimal getQuantity() {
		return quantity;
	}

	public void setQuantity(BigDecimal bigDecimal) {
		this.quantity = bigDecimal;
	}

	public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    

    public BigDecimal getTotalPrice() {
		return totalPrice;
	}

	public BigDecimal getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

	public void setTotalPrice(BigDecimal totalPrice) {
	    this.totalPrice = totalPrice;
		
	}
	
	

}