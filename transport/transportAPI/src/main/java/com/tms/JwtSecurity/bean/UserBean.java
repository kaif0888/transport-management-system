package com.tms.JwtSecurity.bean;

import com.tms.JwtSecurity.entity.Role;

public class UserBean {
    private String userId;
    private String firstName;
    private String secondName;
    private String email;
    private String branchIds;
    private String branchName;
    private String passwordResetCode;
    public String getBranchName() {
		return branchName;
	}
	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}
	private String password;
    private Role role;
    
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getSecondName() {
		return secondName;
	}
	public void setSecondName(String secondName) {
		this.secondName = secondName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getBranchIds() {
		return branchIds;
	}
	public void setBranchIds(String branchIds) {
		this.branchIds = branchIds;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public Role getRole() {
		return role;
	}
	public void setRole(Role role) {
		this.role = role;
	}
	public String getPasswordResetCode() {
		return passwordResetCode;
	}
	public void setPasswordResetCode(String passwordResetCode) {
		this.passwordResetCode = passwordResetCode;
	}
   
    
    
}