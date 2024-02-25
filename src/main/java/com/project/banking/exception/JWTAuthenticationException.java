package com.project.banking.exception;

import org.springframework.security.core.AuthenticationException;

public abstract class JWTAuthenticationException extends AuthenticationException {
	public JWTAuthenticationException(String message) {
		super(message);
	}
}
