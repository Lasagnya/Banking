package com.project.banking.controller;

import com.project.banking.service.UserService;
import com.project.banking.to.front.AuthenticationDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/person")
public class PersonController {
	private final UserService userService;

	@Autowired
	public PersonController(UserService userService) {
		this.userService = userService;
	}

	@GetMapping("/user")
	public AuthenticationDTO userMethod() {
		AuthenticationDTO auth = userService.authenticatedUser();
		return auth;
	}

	@GetMapping("/admin")
	public String adminMethod() {
		AuthenticationDTO auth = userService.authenticatedAdmin();
		return "admin: \n" + auth.toString();
	}

	@GetMapping("/guest")
	public String guestMethod() {
		return "test";
	}
}
