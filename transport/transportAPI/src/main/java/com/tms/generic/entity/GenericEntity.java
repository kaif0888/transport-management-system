package com.tms.generic.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class GenericEntity {

	@CreatedBy
	@Column(name = "CREATED_BY")
	protected String createdBy;

	@CreatedDate
	@Column(name = "CREATED_DATE")
	@JsonFormat(pattern = "yyyy-MM-dd")
	protected LocalDateTime createdDate;

	@LastModifiedBy
	@Column(name = "MODIFIED_BY")
	protected String lastModifiedBy;

	@LastModifiedDate
	@Column(name = "MODIFIED_DATE")
	@JsonFormat(pattern = "yyyy-MM-dd")
	protected LocalDateTime lastModifiedDate;

    @Column(name = "branchIds")
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
