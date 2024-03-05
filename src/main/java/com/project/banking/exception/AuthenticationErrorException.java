package com.project.banking.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class AuthenticationErrorException extends ResponseStatusException {
	public AuthenticationErrorException(String reason) {
		super(HttpStatus.UNAUTHORIZED, reason);
	}
}
