package com.tms.branch.entity;

import com.tms.generic.entity.GenericEntity;
import com.tms.location.entity.LocationEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "BRANCH")
public class BranchEntity extends GenericEntity {

    @Id
    @Column(name = "branch_id", nullable = false, length = 50)
    private String branchId;

    @Column(name = "branch_name", nullable = false, length = 100)
    private String branchName;

    @Column(name = "branch_type", length = 30)
    private String branchType; 

    @ManyToOne
    @JoinColumn(name = "location_id", nullable = false)
    private LocationEntity location;

    @Column(name = "contact_info", length = 255)
    private String contactInfo;

    @Column(name = "used_capacity")
    private Integer usedCapacity;

    @Column(name = "total_capacity")
    private Integer totalCapacity;

    @Column(name = "orders_count")
    private Integer orders;

    @Column(name = "vehicles_count")
    private Integer vehicles;

    @Column(name = "staff_count")
    private Integer staff;

    @Column(name = "status", length = 20)
    private String status; 
    
    @Column(name = "location_Address")
    private String locationAddress;



    public String getBranchId() {
        return branchId;
    }

    public void setBranchId(String branchId) {
        this.branchId = branchId;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getBranchType() {
        return branchType;
    }

    public void setBranchType(String branchType) {
        this.branchType = branchType;
    }

    public LocationEntity getLocation() {
        return location;
    }

    public void setLocation(LocationEntity location) {
        this.location = location;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    public Integer getUsedCapacity() {
        return usedCapacity;
    }

    public void setUsedCapacity(Integer usedCapacity) {
        this.usedCapacity = usedCapacity;
    }

    public Integer getTotalCapacity() {
        return totalCapacity;
    }

    public void setTotalCapacity(Integer totalCapacity) {
        this.totalCapacity = totalCapacity;
    }

    public Integer getOrders() {
        return orders;
    }

    public void setOrders(Integer orders) {
        this.orders = orders;
    }

    public Integer getVehicles() {
        return vehicles;
    }

    public void setVehicles(Integer vehicles) {
        this.vehicles = vehicles;
    }

    public Integer getStaff() {
        return staff;
    }

    public void setStaff(Integer staff) {
        this.staff = staff;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

	public String getLocationAddress() {
		return locationAddress;
	}

	public void setLocationAddress(String locationAddress) {
		this.locationAddress = locationAddress;
	}
    
}
