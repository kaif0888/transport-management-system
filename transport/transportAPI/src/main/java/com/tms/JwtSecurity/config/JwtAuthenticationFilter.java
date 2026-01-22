package com.tms.JwtSecurity.config;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.tms.JwtSecurity.service.JWTService;
import com.tms.JwtSecurity.service.UserService;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JWTService jwtService;
	private final UserService userService;

	public JwtAuthenticationFilter(JWTService jwtService, UserService userService) {
		super();
		this.jwtService = jwtService;
		this.userService = userService;
	}


	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
	    String path = request.getServletPath();

	    return path.startsWith("/api/v1/auth/")
	    || path.startsWith("/api/auth/otp/")  
        || path.startsWith("/api/dashboard/");
//	            || path.startsWith("/api/dashboard")
//	            || path.startsWith("/swagger-ui/")
//	            || path.startsWith("/v3/api-docs/")
//	            || path.startsWith("/bookingCost/")
//	            || path.startsWith("/branch/")
//	            || path.startsWith("/customer/")
//	            || path.startsWith("/dispatch/")
//	            || path.startsWith("/drivers/")
//	            || path.startsWith("/employee/")
//	            || path.startsWith("/expense/")
//	            || path.startsWith("/expenseType/")
//	            || path.startsWith("/invoice/")
//	            || path.startsWith("/location/")
//	            || path.startsWith("/menu/")
//	            || path.startsWith("/manifest/")
//	            || path.startsWith("/order/")
//	            || path.startsWith("/orderProducts/")
//	            || path.startsWith("/payment/")        
//	            || path.startsWith("/product/")
//	            || path.startsWith("/category/")
//	            || path.startsWith("/vehicleType/") 
//	            || path.startsWith("/vehicles/") 
//	            || path.startsWith("/rental/")
//	            || path.startsWith("/manifest/")
//	            || path.startsWith("/dispatchTracking/");
	    
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		final String auth = request.getHeader("Authorization");
		final String jwt;
		final String userEmail;

		if (StringUtils.isEmpty(auth) || !org.apache.commons.lang3.StringUtils.startsWith(auth, "Bearer ")) {
			filterChain.doFilter(request, response);
			return;
		}
		jwt = auth.substring(7);
		try {
			userEmail = jwtService.extractUsername(jwt);

			if (StringUtils.isNotEmpty(userEmail) && SecurityContextHolder.getContext().getAuthentication() == null) {
				UserDetails userDetails = userService.userDetailsService().loadUserByUsername(userEmail);
				if (jwtService.isTokenValid(jwt, userDetails)) {
					SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
					UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(userDetails,
							null, userDetails.getAuthorities());
					token.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
					securityContext.setAuthentication(token);
					SecurityContextHolder.setContext(securityContext);
				}
			}
			filterChain.doFilter(request, response);
		} catch (ExpiredJwtException e) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.setContentType("application/json");
			response.getWriter().write("{\"error\": \"Token has expired\"}");
		} catch (JwtException | IllegalArgumentException e) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.setContentType("application/json");
			response.getWriter().write("{\"error\": \"Invalid token\"}");
		}
	}

}