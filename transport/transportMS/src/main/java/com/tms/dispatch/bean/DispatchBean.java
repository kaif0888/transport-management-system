package com.tms.dispatch.bean;

public class DispatchBean {
	
    private Long dispatchId;
    private Long vehicleId;
    private Long driverId;
    private String dispatchType;
    private String status;
    
    
	public Long getDispatchId() {
		return dispatchId;
	}
	public void setDispatchId(Long dispatchId) {
		this.dispatchId = dispatchId;
	}
	public Long getVehicleId() {
		return vehicleId;
	}
	public void setVehicleId(Long vehicleId) {
		this.vehicleId = vehicleId;
	}
	public Long getDriverId() {
		return driverId;
	}
	public void setDriverId(Long driverId) {
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
    
    
  }
