package com.tms.driver.been;

import java.util.Date;

import com.tms.generic.bean.GenericBean;

public class AvalaibleDriverBean extends GenericBean{
	  private String driverId;
	    private String name;
	    private String licenceNumber;
	    private Date licenceExpiryDate;
	    private String contactNumber;
		public AvalaibleDriverBean() {
			super();
			// TODO Auto-generated constructor stub
		}
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
		public String getLicenceNumber() {
			return licenceNumber;
		}
		public void setLicenceNumber(String licenceNumber) {
			this.licenceNumber = licenceNumber;
		}
		public Date getLicenceExpiryDate() {
			return licenceExpiryDate;
		}
		public void setLicenceExpiryDate(Date licenceExpiryDate) {
			this.licenceExpiryDate = licenceExpiryDate;
		}
		public String getContactNumber() {
			return contactNumber;
		}
		public void setContactNumber(String contactNumber) {
			this.contactNumber = contactNumber;
		}
	    
	    
}
