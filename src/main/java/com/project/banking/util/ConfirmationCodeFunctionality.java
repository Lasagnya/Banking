package com.project.banking.util;

import com.project.banking.domain.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class ConfirmationCodeFunctionality {
	private final ApplicationContext context;

	@Autowired
	public ConfirmationCodeFunctionality(ApplicationContext context) {
		this.context = context;
	}

	public Integer generateConfirmationCode(Transaction transaction) {
		Random random = new Random();
		return random.nextInt(10);
	}

	public boolean verifyConfirmationCode(Transaction receivedTransaction, Integer referenceCode) {
		return receivedTransaction.getConfirmationCode().equals(referenceCode);
	}

	public void expiryTimer(Transaction transaction) {
		ExpiryFunctionality expiryFunctionality = context.getBean(ExpiryFunctionality.class);
		expiryFunctionality.setTransaction(transaction);
		expiryFunctionality.run();
	}
}
