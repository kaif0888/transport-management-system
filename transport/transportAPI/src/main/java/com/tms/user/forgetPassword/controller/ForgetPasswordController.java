package com.tms.user.forgetPassword.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tms.user.forgetPassword.service.ForgotPasswordService;

@RestController
@RequestMapping(value = "/api/user")
public class ForgetPasswordController {

	@Autowired
	private ForgotPasswordService forgotPasswordService;

	@PostMapping("/forgot-password")
	public ResponseEntity<String> forgotPassword(@RequestBody Map<String, String> request) {
		try {
			forgotPasswordService.sendResetEmail(request.get("email"));
			return ResponseEntity.ok("Reset link sent to your email.");
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@PostMapping("/reset-password")
	public ResponseEntity<String> resetPassword(@RequestBody Map<String, String> request) {
		try {
			forgotPasswordService.resetPassword(request.get("token"), request.get("newPassword"));
			return ResponseEntity.ok("Password reset successfully.");
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

}
