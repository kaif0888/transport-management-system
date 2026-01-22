package com.tms.location.entity;

import jakarta.persistence.*;


@Entity
@Table(name = "LOCATION")
public class LocationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "locationId")
    private Long locationId;

    @Column(name = "locationName")
    private String locationName;

    @Column(name = "locationAddress")
    private String locationAddress;
    
    

	public Long getLocationId() {
		return locationId;
	}

	public void setLocationId(Long locationId) {
		this.locationId = locationId;
	}

	public String getLocationName() {
		return locationName;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}

	public String getLocationAddress() {
		return locationAddress;
	}

	public void setLocationAddress(String locationAddress) {
		this.locationAddress = locationAddress;
	} 
    
}
