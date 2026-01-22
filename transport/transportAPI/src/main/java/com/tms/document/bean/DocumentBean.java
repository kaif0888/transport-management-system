package com.tms.document.bean;

import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tms.generic.bean.GenericBean;

public class DocumentBean extends GenericBean {
    private Long id;
    private String documentId;
    private String name;
    private String type;
    private String fileUrl;
    private String documentName;
    private String documentStatus;
    @JsonIgnore
    private MultipartFile licenseFile;
    
    
    
    
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getDocumentId() {
		return documentId;
	}
	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getFileUrl() {
		return fileUrl;
	}
	public void setFileUrl(String fileUrl) {
		this.fileUrl = fileUrl;
	}
	public String getDocumentName() {
		return documentName;
	}
	public void setDocumentName(String documentName) {
		this.documentName = documentName;
	}
	public String getDocumentStatus() {
		return documentStatus;
	}
	public void setDocumentStatus(String documentStatus) {
		this.documentStatus = documentStatus;
	}
	public MultipartFile getLicenseFile() {
		return licenseFile;
	}
	public void setLicenseFile(MultipartFile licenseFile) {
		this.licenseFile = licenseFile;
	} 
	
	
	
	

   
}
