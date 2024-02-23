package com.project.banking.controller;

import com.project.banking.domain.User;
import com.project.banking.service.AuthenticationService;
import com.project.banking.to.front.AuthenticationDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {
	private final AuthenticationService authenticationService;

	@Autowired
	public AuthenticationController(AuthenticationService authenticationService) {
		this.authenticationService = authenticationService;
	}

	@PostMapping("/registration")
	public Map<String, String> create(@RequestBody AuthenticationDTO auth) {
		User user = new User(auth);
		return authenticationService.register(user);
	}

	@PostMapping("/login")
	public Map<String, String> login(@RequestBody AuthenticationDTO auth) {
		User user = new User(auth);
		return authenticationService.login(user);
	}
}
