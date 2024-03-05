package com.project.banking.exception;

public class InvalidJWTException extends JWTAuthenticationException {
	public InvalidJWTException(String message) {
		super(message);
	}
}
