package com.tms.driver.been;

import java.time.LocalDate;
import java.util.List;

import com.tms.document.bean.DocumentBean;


public class DriverDocumentResponseBean {
	  private String driverId;
	    private String name;
	    private String licenseNumber;
	    private LocalDate licenseExpiry;
	    private String contactNumber;
	    private String assignedVehicleId;
	    private List<DocumentBean> documentIds;
	    
	    
		public String getDriverId() {
			return driverId;
		}
		public void setDriverId(String driverId) {
			this.driverId = driverId;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getLicenseNumber() {
			return licenseNumber;
		}
		public void setLicenseNumber(String licenseNumber) {
			this.licenseNumber = licenseNumber;
		}
		public LocalDate getLicenseExpiry() {
			return licenseExpiry;
		}
		public void setLicenseExpiry(LocalDate licenseExpiry) {
			this.licenseExpiry = licenseExpiry;
		}
		public String getContactNumber() {
			return contactNumber;
		}
		public void setContactNumber(String contactNumber) {
			this.contactNumber = contactNumber;
		}
		public String getAssignedVehicleId() {
			return assignedVehicleId;
		}
		public void setAssignedVehicleId(String assignedVehicleId) {
			this.assignedVehicleId = assignedVehicleId;
		}
		public List<DocumentBean> getDocumentIds() {
			return documentIds;
		}
		public void setDocumentIds(List<DocumentBean> documentIds) {
			this.documentIds = documentIds;
		}
	
	    
	    
	    
}
