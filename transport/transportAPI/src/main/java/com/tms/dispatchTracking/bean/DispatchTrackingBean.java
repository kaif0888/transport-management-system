package com.tms.dispatchTracking.bean;

import com.tms.generic.bean.GenericBean;
import com.tms.location.entity.LocationEntity;

public class DispatchTrackingBean extends GenericBean{
	
   private String trackingId;
   private String dispatchId;
   private String orderId;
   private String activeLocation;
   private String activeLocationName;
   private String timeStamp;
   private String status;
   private long totalOrder;
   private long deliveredOrders;
   private long pendingOrders;
   private String customerNumber;
   private String receiverName;
   private String invoiceId;
   private String invoiceNumber;
   private boolean invoiceGenerated;
   private String invoicePath;
   private String vehiclNumber;



public String getTrackingId() {
	return trackingId;
}
public void setTrackingId(String trackingId) {
	this.trackingId = trackingId;
}
public String getDispatchId() {
	return dispatchId;
}
public void setDispatchId(String dispatchId) {
	this.dispatchId = dispatchId;
}
public String getOrderId() {
	return orderId;
}
public void setOrderId(String orderId) {
	this.orderId = orderId;
}
public String getActiveLocation() {
	return activeLocation;
}
public void setActiveLocation(String activeLocation) {
	this.activeLocation = activeLocation;
}
public String getActiveLocationName() {
	return activeLocationName;
}
public void setActiveLocationName(String activeLocationName) {
	this.activeLocationName = activeLocationName;
}
public String getTimeStamp() {
	return timeStamp;
}
public void setTimeStamp(String timeStamp) {
	this.timeStamp = timeStamp;
}
public String getStatus() {
	return status;
}
public void setStatus(String status) {
	this.status = status;
}
public long getTotalOrder() {
	return totalOrder;
}
public void setTotalOrder(long totalOrder) {
	this.totalOrder = totalOrder;
}
public long getDeliveredOrders() {
	return deliveredOrders;
}
public void setDeliveredOrders(long deliveredOrders) {
	this.deliveredOrders = deliveredOrders;
}
public long getPendingOrders() {
	return pendingOrders;
}
public void setPendingOrders(long pendingOrders) {
	this.pendingOrders = pendingOrders;
}
public String getCustomerNumber() {
	return customerNumber;
}
public void setCustomerNumber(String customerNumber) {
	this.customerNumber = customerNumber;
}
public String getReceiverName() {
	return receiverName;
}
public void setReceiverName(String receiverName) {
	this.receiverName = receiverName;
}
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
public boolean isInvoiceGenerated() {
	return invoiceGenerated;
}
public void setInvoiceGenerated(boolean invoiceGenerated) {
	this.invoiceGenerated = invoiceGenerated;
}
public String getInvoicePath() {
	return invoicePath;
}
public void setInvoicePath(String invoicePath) {
	this.invoicePath = invoicePath;
}
public String getVehiclNumber() {
	return vehiclNumber;
}
public void setVehiclNumber(String vehiclNumber) {
	this.vehiclNumber = vehiclNumber;
}
   
   


}
