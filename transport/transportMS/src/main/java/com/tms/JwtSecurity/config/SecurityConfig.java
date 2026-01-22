package com.tms.JwtSecurity.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.tms.JwtSecurity.entity.Role;
import com.tms.JwtSecurity.service.UserService;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

	private final JwtAuthenticationFilter jwtFilter;
	private final UserService userService;

	public SecurityConfig(JwtAuthenticationFilter jwtFilter, UserService userService) {
		this.jwtFilter = jwtFilter;
		this.userService = userService;
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
	    http.csrf(AbstractHttpConfigurer::disable)
	        .authorizeHttpRequests(auth -> auth
	            // Publicly accessible endpoints
	                .requestMatchers("/api/v1/auth/**").permitAll()
	                .requestMatchers("/api/dashboard/**").permitAll()
	                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
	                .requestMatchers("/vehiclestms/**").permitAll()
	                .requestMatchers("/drivers/**").permitAll()
	                .requestMatchers("/drivers/listVehicles/**").permitAll()
	                .requestMatchers("/rental/**").permitAll()
	                .requestMatchers("/employee/**").permitAll()
	                .requestMatchers("/assign-driver/**").permitAll()
	                .requestMatchers("/payment/**").permitAll()
	                .requestMatchers("/invoice/**").permitAll()
	                .requestMatchers("/dispatch/**").permitAll()
	                .requestMatchers("/expense/**").permitAll()
	                .requestMatchers("/expenseType/**").permitAll()
	                .requestMatchers("/bookingCost/**").permitAll()
	                .requestMatchers("/customer/**").permitAll()
	                .requestMatchers("/order/**").permitAll()
	                .requestMatchers("/orderProducts/**").permitAll() 
	                .requestMatchers("/product/**").permitAll()
	                .requestMatchers("/category/**").permitAll()       
	                .requestMatchers("/location/**").permitAll()
	                .requestMatchers("/menu/**").permitAll()           
	                .requestMatchers("/branch/**").permitAll()       

	            // Role-based access  
	            .requestMatchers("/api/v1/admin/**").hasAuthority("ADMIN")
	            .requestMatchers("/api/v1/admin").hasAuthority(Role.ADMIN.name())
	            .requestMatchers("/api/v1/user").hasAuthority(Role.USER.name())
					.requestMatchers("/dispatch/**").permitAll()
					.requestMatchers("/expense/**").permitAll()
					.requestMatchers("/expenseType/**").permitAll()
					.requestMatchers("/bookingCost/**").permitAll()
					.requestMatchers("/customer/**").permitAll()
					.requestMatchers("/order/**").permitAll()
					.requestMatchers("/location/**").permitAll()
				
					
					.requestMatchers("/api/v1/admin/**").hasAuthority("ADMIN")
					  

	            // All other endpoints require authentication
	            .anyRequest().authenticated()
	        )
	        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
	        .authenticationProvider(authenticationProvider())
	        .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

	    return http.build();
	}


	@Bean
	public AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
		auth.setUserDetailsService(userService.userDetailsService());
		auth.setPasswordEncoder(passwordEncoder());
		return auth;
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}
}
