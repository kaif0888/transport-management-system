package com.tms.JwtSecurity.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import com.tms.JwtSecurity.dto.JwtAuthenticationResponse;
import com.tms.JwtSecurity.dto.RefreshTokenRequest;
import com.tms.JwtSecurity.dto.SignInRequest;
import com.tms.JwtSecurity.dto.SignUpRequest;
import com.tms.JwtSecurity.entity.Role;
import com.tms.JwtSecurity.entity.User;
import com.tms.JwtSecurity.repository.UserRepository;



@Service
public class AuthenticationServiceImpl implements AuthenticationService{
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final AuthenticationManager manager;
	private final JWTService jwtService;
	
	
	

	public AuthenticationServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder,
			AuthenticationManager manager, JWTService jwtService) {
		super();
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.manager = manager;
		this.jwtService = jwtService;
	}
	
	private String generateUniqueUserId() {
	    String prefix = "US-";
	    String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
	    String fullPrefix = prefix + dateStr + "-";

	    List<User> todayUsers = userRepository.findByUserIdStartingWith(fullPrefix);

	    int maxSeq = todayUsers.stream()
	        .map(u -> u.getUserId().substring(fullPrefix.length()))
	        .mapToInt(seq -> {
	            try {
	                return Integer.parseInt(seq);
	            } catch (NumberFormatException e) {
	                return 0;
	            }
	        })
	        .max()
	        .orElse(0);

	    int nextSeq = maxSeq + 1;
	    String formattedSeq = String.format("%03d", nextSeq);

	    return fullPrefix + formattedSeq;
	}


	public User signUp(SignUpRequest signUpRequest) {
		User user= new User();
		user.setUserId(generateUniqueUserId());
		user.setEmail(signUpRequest.getEmail());
		user.setFirstName(signUpRequest.getFirstName());
		user.setSecondName(signUpRequest.getLastName());
		user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
		user.setBranchIds(signUpRequest.getBranchIds());
		user.setRole(Role.valueOf(signUpRequest.getRole()));
		
		return userRepository.save(user);
	}
	
	public JwtAuthenticationResponse signin(@RequestBody SignInRequest req) {
		manager.authenticate(new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword()));
		var user=userRepository.findByEmail(req.getEmail()).orElseThrow(()->new IllegalArgumentException("Inavlid Email or Password"));
		var jwt=jwtService.generateToken(user);
		var refreshToken=jwtService.generateRefreshToken(new HashMap<>(),user);
		
		JwtAuthenticationResponse jwtResponse=new JwtAuthenticationResponse();
		jwtResponse.setToken(jwt);
		jwtResponse.setRefreshToken(refreshToken);
		jwtResponse.setEmail(user.getEmail());
		jwtResponse.setFirstName(user.getFirstName());
		jwtResponse.setSecondName(user.getSecondName());
		jwtResponse.setBranchIds(user.getBranchIds());
		jwtResponse.setRole(user.getRole());
		return jwtResponse;
	}
	
	public JwtAuthenticationResponse refreshToken(@RequestBody RefreshTokenRequest req) {
		String userEmail=jwtService.extractUsername(req.getToken());
		User user=userRepository.findByEmail(userEmail).orElseThrow();
		if(jwtService.isTokenValid(req.getToken(), user)) {
			var jwt=jwtService.generateToken(user);
			
			JwtAuthenticationResponse jwtResponse=new JwtAuthenticationResponse();
			jwtResponse.setToken(jwt);
			jwtResponse.setRefreshToken(req.getToken());
			return jwtResponse;
		}
		return null;
	}
}
