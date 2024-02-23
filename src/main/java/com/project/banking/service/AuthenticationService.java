package com.project.banking.service;

import com.project.banking.domain.User;

import java.util.Map;

public interface AuthenticationService {
	Map<String, String> register(User user);

	Map<String, String> login(User user);
}
