package com.tms.user.forgetPassword.service;

public interface ForgotPasswordService {

	public void sendResetEmail(String email);

	public void resetPassword(String token, String newPassword) throws Exception;
}
