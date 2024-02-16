package com.project.banking.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ConfirmationInputException extends ResponseStatusException {
	public ConfirmationInputException(String reason) {
		super(HttpStatus.CONFLICT, reason);
	}
}
