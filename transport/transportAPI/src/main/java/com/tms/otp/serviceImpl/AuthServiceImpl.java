package com.tms.otp.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tms.JwtSecurity.service.JWTService;  
import com.tms.otp.entity.UserOtp;
import com.tms.otp.repository.UserOtpRepository;
import com.tms.otp.service.AuthService;
import com.tms.otp.service.OtpService;

@Service
public class AuthServiceImpl implements AuthService {
    
    @Autowired
    private UserOtpRepository userRepository;
    
    @Autowired
    private OtpService otpService;
    
    @Autowired
    private JWTService jwtService;  
    
    @Override
    @Transactional
    public String login(String mobileNumber, String otp) {
        if (!otpService.verifyOtp(mobileNumber, otp)) {
            throw new RuntimeException("Invalid OTP");
        }
        
        UserOtp user = userRepository.findByMobileNumber(mobileNumber)
                .orElseGet(() -> {
                    UserOtp newUser = new UserOtp();
                    newUser.setMobileNumber(mobileNumber);
                    return userRepository.save(newUser);
                });
        
        return jwtService.generateToken(user);  
    }
}