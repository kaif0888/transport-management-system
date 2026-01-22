package com.tms.otp.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tms.otp.entity.Otp;

public interface OtpRepository extends JpaRepository<Otp, Long> {
	Optional<Otp> findByMobileNumber(String mobileNumber);
	
	 Optional<Otp> findTopByMobileNumberOrderByExpiryTimeDesc(String mobileNumber);

	    void deleteByMobileNumber(String mobileNumber);
}
