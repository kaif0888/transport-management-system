package com.tms.JwtSecurity.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.userdetails.UserDetails;


public interface JWTService {
	String generateToken(UserDetails userDetails);
	String extractUsername(String token);
	 boolean isTokenExpired(String token);
	 public boolean isTokenValid(String token,UserDetails userDetails);
	 public String generateRefreshToken(Map<String,Object> extraClaims,UserDetails userDetails);
}
