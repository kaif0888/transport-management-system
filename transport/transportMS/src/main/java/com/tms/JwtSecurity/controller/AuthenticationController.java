package com.tms.JwtSecurity.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tms.JwtSecurity.dto.JwtAuthenticationResponse;
import com.tms.JwtSecurity.dto.RefreshTokenRequest;
import com.tms.JwtSecurity.dto.SignInRequest;
import com.tms.JwtSecurity.dto.SignUpRequest;
import com.tms.JwtSecurity.entity.User;
import com.tms.JwtSecurity.service.AuthenticationService;
import com.tms.constant.Constants;


@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = Constants.CROSS_ORIGIN)
public class AuthenticationController {
	private final AuthenticationService authenticationService;
	
	
	
	public AuthenticationController(AuthenticationService authenticationService) {
		super();
		this.authenticationService = authenticationService;
	}



	@PostMapping("/signup")
	public ResponseEntity<User> signUp(@RequestBody SignUpRequest req) {
		return ResponseEntity.ok(authenticationService.signUp(req));
	}
	
	@PostMapping("/signin")
	public ResponseEntity<JwtAuthenticationResponse> signIn(@RequestBody SignInRequest req) {
		return ResponseEntity.ok(authenticationService.signin(req));
	}
	
	@PostMapping("/refresh")
	public ResponseEntity<JwtAuthenticationResponse> refresh(@RequestBody RefreshTokenRequest req) {
		return ResponseEntity.ok(authenticationService.refreshToken(req));
	}
}
