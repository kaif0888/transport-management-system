package com.tms.vehicletype.Bean;

import com.tms.generic.bean.GenericBean;

public class VehicleTypeBean extends GenericBean{
  
	 private String vehicleTypeId;
	 
	 private String vehicleTypeName;
	 
	 private String description;

	public String getVehicleTypeId() {
		return vehicleTypeId;
	}

	public void setVehicleTypeId(String vehicleTypeId) {
		this.vehicleTypeId = vehicleTypeId;
	}

	public String getVehicleTypeName() {
		return vehicleTypeName;
	}

	public void setVehicleTypeName(String vehicleTypeName) {
		this.vehicleTypeName = vehicleTypeName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	
	 
	 
}
