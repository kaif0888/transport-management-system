package com.tms.JwtSecurity.service;

import org.springframework.web.bind.annotation.RequestBody;

import com.tms.JwtSecurity.dto.JwtAuthenticationResponse;
import com.tms.JwtSecurity.dto.RefreshTokenRequest;
import com.tms.JwtSecurity.dto.SignInRequest;
import com.tms.JwtSecurity.dto.SignUpRequest;
import com.tms.JwtSecurity.entity.User;



public interface AuthenticationService {
	public User signUp(SignUpRequest signUpRequest);
	public JwtAuthenticationResponse signin(@RequestBody SignInRequest req);
	public JwtAuthenticationResponse refreshToken(@RequestBody RefreshTokenRequest req);
}
