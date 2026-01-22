package com.tms.otp.serviceImpl;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tms.otp.entity.Otp;
import com.tms.otp.repository.OtpGenerator;
import com.tms.otp.repository.OtpRepository;
import com.tms.otp.service.OtpService;
import com.tms.sms.service.SmsService;

@Service
public class OtpServiceImpl implements OtpService {

    @Autowired
    private OtpRepository otpRepository;

    @Autowired
    private OtpGenerator otpGenerator;
    
    @Autowired
    private SmsService smsService;

    public void sendOtp(String mobileNumber) {
    	
    	otpRepository.deleteByMobileNumber(mobileNumber);

        String otp = otpGenerator.generateOtp();

        Otp otpEntity = new Otp();
        otpEntity.setMobileNumber(mobileNumber);
        otpEntity.setOtp(otp);
        otpEntity.setExpiryTime(LocalDateTime.now().plusMinutes(5));

        otpRepository.save(otpEntity);

        // SMS integration here
        System.out.println("OTP for " + mobileNumber + " is: " + otp);
        
        // Send otp using twilio
        String message = "Your login OTP is " + otp + ". Valid for 5 minutes.";
        smsService.sendSms(mobileNumber, message);

    }

    public boolean verifyOtp(String mobileNumber, String otp) {

        Otp otpEntity = otpRepository.findTopByMobileNumberOrderByExpiryTimeDesc(mobileNumber)
                .orElseThrow(() -> new RuntimeException("OTP not found"));

        if (otpEntity.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP expired");
        }
        if (!otpEntity.getOtp().equals(otp)) {
            throw new RuntimeException("Invalid OTP");
        }

        // OPTIONAL: delete OTP after successful verification
        otpRepository.delete(otpEntity);

        return true;

//        return otpEntity.getOtp().equals(otp);
    }
	

}
