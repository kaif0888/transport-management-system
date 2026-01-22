package com.tms.JwtSecurity.dto;



public class SignUpRequest {
	private String firstName;
	private String lastName;
	private String email;
	private String password;
	private String role;
	private String branchIds;
	
	
	public SignUpRequest() {
		super();
	}


	public String getFirstName() {
		return firstName;
	}


	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}


	public String getLastName() {
		return lastName;
	}


	public void setLastName(String lastName) {
		this.lastName = lastName;
	}


	public String getEmail() {
		return email;
	}


	public void setEmail(String email) {
		this.email = email;
	}


	public String getPassword() {
		return password;
	}


	public void setPassword(String password) {
		this.password = password;
	}


	public String getRole() {
		return role;
	}


	public void setRole(String role) {
		this.role = role;
	}


	public String getBranchIds() {
		return branchIds;
	}


	public void setBranchIds(String branchIds) {
		this.branchIds = branchIds;
	}

}
