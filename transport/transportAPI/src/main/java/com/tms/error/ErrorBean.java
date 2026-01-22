package com.tms.error;

import org.springframework.stereotype.Component;

@Component
public class ErrorBean {
	private boolean success;
	private String message;

	// With their getters and setters
	public boolean isSuccess() { return success; }
	public void setSuccess(boolean success) { this.success = success; }

	public String getMessage() { return message; }
	public void setMessage(String message) { this.message = message; }

}
