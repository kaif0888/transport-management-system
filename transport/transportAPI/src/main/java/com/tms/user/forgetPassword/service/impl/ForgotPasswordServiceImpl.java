package com.tms.user.forgetPassword.service.impl;

import java.time.LocalDateTime;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.tms.JwtSecurity.entity.User;
import com.tms.JwtSecurity.repository.UserRepository;
import com.tms.user.forgetPassword.service.ForgotPasswordService;

@Service
public class ForgotPasswordServiceImpl implements ForgotPasswordService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private JavaMailSender mailSender;

	@Autowired
	private PasswordEncoder passwordEncoder;

//	@Override
//	public void sendResetEmail(String email) {
//		User user = null;
//		try {
//			user = userRepository.findByEmail(email).orElseThrow(() -> new Exception("User not found"));
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		String token = UUID.randomUUID().toString();
//		user.setResetToken(token);
//		user.setTokenExpiry(LocalDateTime.now().plusMinutes(15));
//		userRepository.save(user);
//
//		sendEmail(user.getEmail(),token);
//
//	}
	
	@Override
	public void sendResetEmail(String email) {
	    try {
	        User user = userRepository.findByEmail(email)
	            .orElseThrow(() -> new RuntimeException("User not found"));

	        // Generate 6-digit reset code from nanoseconds
	        int nano = LocalDateTime.now().getNano();
	        String resetCode = String.format("%06d", nano % 1_000_000); // Last 6 digits

	        user.setPasswordResetCode(resetCode); // Set in DB field
	        user.setTokenExpiry(LocalDateTime.now().plusMinutes(15)); // Optional: Expiry logic

	        userRepository.save(user); // Persist changes

	        sendEmail(user.getEmail(), resetCode);

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}


//	private void sendEmail(String email,String token) {
//		SimpleMailMessage message = new SimpleMailMessage();
//		String resetLink = "http://localhost:8080/reset-password?token=" + token;
//		message.setTo(email);
//		message.setSubject("Reset Your Password");
////		message.setText("Click the link to reset your password: "+resetLink);
//		message.setText("Hey Nazim how are you");
//		mailSender.send(message);
//
//	}
	
	private void sendEmail(String email, String resetCode) {
	    SimpleMailMessage message = new SimpleMailMessage();
	    message.setTo(email);
	    message.setSubject("Your Password Reset Code");
	    message.setText("Use the following 6-digit code to reset your password: " + resetCode);
	    mailSender.send(message);
	}


//	@Override
//	public void resetPassword(String token, String newPassword) throws Exception {
//		User user = null;
//		try {
//			user = userRepository.findByResetToken(token).orElseThrow(() -> new Exception("Invalid or expired token"));
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		if (user.getTokenExpiry().isBefore(LocalDateTime.now())) {
//			throw new Exception("Token has expired.");
//		}
//
//		user.setPassword(passwordEncoder.encode(newPassword));
//		user.setResetToken(null); // invalidate token
//		user.setTokenExpiry(null);
//		userRepository.save(user);
//
//	}

	@Override
	public void resetPassword(String resetCode, String newPassword) throws Exception {
	    // Find user or throw if not found
	    User user = userRepository.findByPasswordResetCode(resetCode)
	            .orElseThrow(() -> new Exception("Invalid or expired token"));

	    // Check token expiry
	    if (user.getTokenExpiry() == null || user.getTokenExpiry().isBefore(LocalDateTime.now())) {
	        throw new Exception("Token has expired.");
	    }

	    // Reset password
	    user.setPassword(passwordEncoder.encode(newPassword));
	    user.setResetToken(null); // Invalidate token
	    user.setTokenExpiry(null);
	    userRepository.save(user);
	}

}
