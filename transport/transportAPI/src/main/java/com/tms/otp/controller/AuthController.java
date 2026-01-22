package com.tms.otp.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tms.otp.service.AuthService;
import com.tms.otp.service.OtpService;

@RestController
@RequestMapping("/api/auth/otp")
public class AuthController {

    @Autowired
    private OtpService otpService;

    @Autowired
    private AuthService authService;

//    @PostMapping("/send-otp")
//    public ResponseEntity<?> sendOtp(@RequestParam String mobileNumber) {
//        otpService.sendOtp(mobileNumber);
//        return ResponseEntity.ok("OTP sent successfully");
//    }
    
    @PostMapping("/send-otp")
    @Transactional
    public ResponseEntity<?> sendOtp(@RequestBody Map<String, String> request) {  
        String mobileNumber = request.get("mobile"); 
        otpService.sendOtp(mobileNumber);
        return ResponseEntity.ok(Map.of("message", "OTP sent successfully"));
    }
    
    @PostMapping("/verify-otp")  
    public ResponseEntity<?> verifyOtp(@RequestBody Map<String, String> request) {
        String token = authService.login(
            request.get("mobileNumber"),  
            request.get("otp")  
        );
        return ResponseEntity.ok(Map.of("token", token));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestParam String mobileNumber,
            @RequestParam String otp) {

        String token = authService.login(mobileNumber, otp);
        return ResponseEntity.ok(Map.of("token", token));
    }
}

