package com.tms.generic.bean;

import java.time.LocalDateTime;

public class GenericBean {

	protected String createdBy;
	protected LocalDateTime createdDate;
	protected String lastModifiedBy;
	protected LocalDateTime lastModifiedDate;
    private String branchIds;
    
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public LocalDateTime getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}
	public String getLastModifiedBy() {
		return lastModifiedBy;
	}
	public void setLastModifiedBy(String lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}
	public LocalDateTime getLastModifiedDate() {
		return lastModifiedDate;
	}
	public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}
	public String getBranchIds() {
		return branchIds;
	}
	public void setBranchIds(String branchIds) {
		this.branchIds = branchIds;
	}
	
    
}
