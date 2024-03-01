package com.project.banking.service.impl;

import com.project.banking.domain.User;
import com.project.banking.exception.AuthenticationErrorException;
import com.project.banking.security.JWTUtil;
import com.project.banking.service.AuthenticationService;
import com.project.banking.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {
	private final UserService userService;
	private final JWTUtil jwtUtil;
	private final PasswordEncoder passwordEncoder;
	private final AuthenticationManager authenticationManager;

	@Autowired
	public AuthenticationServiceImpl(UserService userService, JWTUtil jwtUtil, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager) {
		this.userService = userService;
		this.jwtUtil = jwtUtil;
		this.passwordEncoder = passwordEncoder;
		this.authenticationManager = authenticationManager;
	}

	@Override
	public Map<String, String> register(User user) {
		user.setRole("ROLE_USER");
		byte[] hashBytes = passwordEncoder.encode(user.getRawPassword()).getBytes(StandardCharsets.ISO_8859_1);
		user.setEncodedPassword(hashBytes);
		userService.save(user);
		String token = jwtUtil.generateToken(user.getName());
		return Map.of("jwt-token", token);
	}

	@Override
	public Map<String, String> login(User user) {
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user.getName(), user.getRawPassword());
		try {
			authenticationManager.authenticate(authenticationToken);
		} catch (AuthenticationException e) {
			throw new AuthenticationErrorException("Incorrect credentials");
		}
		String token = jwtUtil.generateToken(authenticationToken.getName());
		return Map.of("jwt-token", token);
	}


}
