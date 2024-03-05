package com.project.banking.exception;

public abstract class TransactionVerificationException extends Exception {
	public TransactionVerificationException() {
		super("Something is wrong");
	}
}
