package com.project.banking.configuration;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.project.banking.exception.InvalidJWTException;
import com.project.banking.security.JWTUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JWTFilter extends OncePerRequestFilter {
	private final JWTUtil jwtUtil;
	private final UserDetailsService userDetailsService;
	private final AuthenticationEntryPoint authenticationEntryPoint;

	@Autowired
	public JWTFilter(JWTUtil jwtUtil, UserDetailsService userDetailsService, AuthenticationEntryPoint authenticationEntryPoint) {
		this.jwtUtil = jwtUtil;
		this.userDetailsService = userDetailsService;
		this.authenticationEntryPoint = authenticationEntryPoint;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		String authHeader = request.getHeader("Authorization");
		if (authHeader != null && !authHeader.isBlank() && authHeader.startsWith("Bearer ")) {
			String jwt = authHeader.substring(7);

			if (jwt.isBlank()) {
				authenticationEntryPoint.commence(request, response, new InvalidJWTException("JWT token is blank"));
//				response.sendError(HttpStatus.BAD_REQUEST.value(), "JWT token is blank");
			} else {
				try {
					String username = jwtUtil.validateTokenAndRetrieveClaim(jwt);
					UserDetails userDetails = userDetailsService.loadUserByUsername(username);
					UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities());

					if (SecurityContextHolder.getContext().getAuthentication() == null) {
						SecurityContextHolder.getContext().setAuthentication(authenticationToken);
					}
				} catch (JWTVerificationException e) {
					authenticationEntryPoint.commence(request, response, new InvalidJWTException("Invalid JWT token"));
//					response.sendError(HttpStatus.BAD_REQUEST.value(), "Invalid JWT token");
				}
			}
		}

		filterChain.doFilter(request, response);
	}
}
