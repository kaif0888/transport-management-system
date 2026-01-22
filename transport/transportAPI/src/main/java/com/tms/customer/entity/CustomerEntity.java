package com.tms.customer.entity;

import com.tms.generic.entity.GenericEntity;
import com.tms.location.entity.LocationEntity;

import jakarta.persistence.*;

@Entity
@Table(name = "Customer")
public class CustomerEntity extends GenericEntity {

    @Id
    @Column(name = "customerId")
    private String customerId;

    @Column(name = "customerName")
    private String customerName;

    @Column(name = "customerInfo")
    private String customerInfo;

    @Column(name = "customerNumber")
    private String customerNumber;

    @Column(name = "customerEmail")
    private String customerEmail;
    
    @Column(name = "localBillingAddress")
    private String localBillingAddress;
    
    @Column(name = "localShippingAddress")
    private String localShippingAddress;

    // Updated: Using @ManyToOne with selective cascading
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "billingAddressId", referencedColumnName = "locationId")
    private LocationEntity billingAddress;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "shippingAddressId", referencedColumnName = "locationId")
    private LocationEntity shippingAddress;

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getCustomerInfo() {
		return customerInfo;
	}

	public void setCustomerInfo(String customerInfo) {
		this.customerInfo = customerInfo;
	}

	public String getCustomerNumber() {
		return customerNumber;
	}

	public void setCustomerNumber(String customerNumber) {
		this.customerNumber = customerNumber;
	}

	public String getCustomerEmail() {
		return customerEmail;
	}

	public void setCustomerEmail(String customerEmail) {
		this.customerEmail = customerEmail;
	}

	public String getLocalBillingAddress() {
		return localBillingAddress;
	}

	public void setLocalBillingAddress(String localBillingAddress) {
		this.localBillingAddress = localBillingAddress;
	}

	public String getLocalShippingAddress() {
		return localShippingAddress;
	}

	public void setLocalShippingAddress(String localShippingAddress) {
		this.localShippingAddress = localShippingAddress;
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

   
}