package com.tms.JwtSecurity.service;

import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService {
	public UserDetailsService userDetailsService();
}
