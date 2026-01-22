package com.tms.vehicletype.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "vehicleType")
public class VehicleTypeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vehicleTypeId")
    private Long vehicleTypeId;

    @Column(name = "vehicleTypeName")
    private String vehicleTypeName;

	public Long getVehicleTypeId() {
		return vehicleTypeId;
	}

	public void setVehicleTypeId(Long vehicleTypeId) {
		this.vehicleTypeId = vehicleTypeId;
	}

	public String getVehicleTypeName() {
		return vehicleTypeName;
	}

	public void setVehicleTypeName(String vehicleTypeName) {
		this.vehicleTypeName = vehicleTypeName;
	}
    
}
