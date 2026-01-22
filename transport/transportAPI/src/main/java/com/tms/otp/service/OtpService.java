package com.tms.otp.service;

public interface OtpService {
	public void sendOtp(String mobileNumber);
	public boolean verifyOtp(String mobileNumber, String otp);
}
