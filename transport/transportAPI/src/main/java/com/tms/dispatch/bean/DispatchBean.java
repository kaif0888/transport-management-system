package com.tms.dispatch.bean;

import com.tms.generic.bean.GenericBean;

public class DispatchBean extends GenericBean {

	private String dispatchId;
	private String vehicleId;
	private String driverId;
	private String dispatchType;
	private String status;
	private String driverName;
	private String model;
	private String registrationNumber;
	private String vehiclNumber;
	
	
	public String getDispatchId() {
		return dispatchId;
	}
	public void setDispatchId(String dispatchId) {
		this.dispatchId = dispatchId;
	}
	public String getVehicleId() {
		return vehicleId;
	}
	public void setVehicleId(String vehicleId) {
		this.vehicleId = vehicleId;
	}
	public String getDriverId() {
		return driverId;
	}
	public void setDriverId(String driverId) {
		this.driverId = driverId;
	}
	public String getDispatchType() {
		return dispatchType;
	}
	public void setDispatchType(String dispatchType) {
		this.dispatchType = dispatchType;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getDriverName() {
		return driverName;
	}
	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}
	public String getRegistrationNumber() {
		return registrationNumber;
	}
	public void setRegistrationNumber(String registrationNumber) {
		this.registrationNumber = registrationNumber;
	}
	public String getVehiclNumber() {
		return vehiclNumber;
	}
	public void setVehiclNumber(String vehiclNumber) {
		this.vehiclNumber = vehiclNumber;
	}

	
  }
