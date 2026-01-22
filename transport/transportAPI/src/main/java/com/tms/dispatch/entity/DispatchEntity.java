package com.tms.dispatch.entity;

import com.tms.driver.entity.DriverEntity;
import com.tms.generic.entity.GenericEntity;
import com.tms.location.entity.LocationEntity;
import com.tms.vehicle.entity.VehicleEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="Dispatch")

public class DispatchEntity extends GenericEntity{
	
    @Id
    @Column(name = "dispatchId")
    private String dispatchId;

    @ManyToOne
    @JoinColumn(name = "vehicleId")
    private VehicleEntity vehicle;

    @ManyToOne
    @JoinColumn(name = "driverId")
    private DriverEntity driver;

    @Column(name = "dispatchType")
    private String dispatchType;

    @Column(name = "status")
    private String status;

	public String getDispatchId() {
		return dispatchId;
	}

	public void setDispatchId(String dispatchId) {
		this.dispatchId = dispatchId;
	}

	public VehicleEntity getVehicle() {
		return vehicle;
	}

	public void setVehicle(VehicleEntity vehicle) {
		this.vehicle = vehicle;
	}

	public DriverEntity getDriver() {
		return driver;
	}

	public void setDriver(DriverEntity driver) {
		this.driver = driver;
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
