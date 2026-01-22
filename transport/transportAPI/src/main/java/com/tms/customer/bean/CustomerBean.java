package com.tms.customer.bean;

import com.tms.generic.bean.GenericBean;
import com.tms.location.bean.LocationBean;

import jakarta.persistence.Column;

public class CustomerBean extends GenericBean {

    private String customerId;
    private String customerName;
    private String customerInfo;
    private String customerNumber;
    private String customerEmail;
    private LocationBean billingAddress;
    private LocationBean shippingAddress;
    private String localBillingAddress;
    private String localShippingAddress;
    
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
	public LocationBean getBillingAddress() {
		return billingAddress;
	}
	public void setBillingAddress(LocationBean billingAddress) {
		this.billingAddress = billingAddress;
	}
	public LocationBean getShippingAddress() {
		return shippingAddress;
	}
	public void setShippingAddress(LocationBean shippingAddress) {
		this.shippingAddress = shippingAddress;
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
    
	
    
}
