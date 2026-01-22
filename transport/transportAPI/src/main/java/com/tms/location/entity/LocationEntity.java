package com.tms.location.entity;

import com.tms.generic.entity.GenericEntity;

import jakarta.persistence.*;

@Entity
@Table(name = "LOCATION")
public class LocationEntity extends GenericEntity {

    @Id
    @Column(name = "locationId")
    private String locationId;

    @Column(name = "locationName")
    private String locationName;
    
    @Column(name = "locationArea")
    private String locationArea;

    @Column(name = "locationAddress")
    private String locationAddress;

    @Column(name = "status")
    private String status;

    @Column(name = "circle")
    private String circle;

    @Column(name = "district")
    private String district;

    @Column(name = "block")
    private String block;

    @Column(name = "state")
    private String state;

    @Column(name = "country")
    private String country;

    @Column(name = "pincode")
    private String pincode;

	public String getLocationId() {
		return locationId;
	}

	public void setLocationId(String locationId) {
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCircle() {
		return circle;
	}

	public void setCircle(String circle) {
		this.circle = circle;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public String getBlock() {
		return block;
	}

	public void setBlock(String block) {
		this.block = block;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getPincode() {
		return pincode;
	}

	public void setPincode(String pincode) {
		this.pincode = pincode;
	}

	public String getLocationArea() {
		return locationArea;
	}

	public void setLocationArea(String locationArea) {
		this.locationArea = locationArea;
	}


    
    

}
