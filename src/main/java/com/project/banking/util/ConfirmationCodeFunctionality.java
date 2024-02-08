package com.project.banking.util;

import com.project.banking.model.database.TransactionDb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class ConfirmationCodeFunctionality {
	private ExpiryFunctionality expiryFunctionality;

	@Autowired
	public void setExpiryFunctionality(ExpiryFunctionality expiryFunctionality) {
		this.expiryFunctionality = expiryFunctionality;
	}

	public Integer generateConfirmationCode(TransactionDb transaction) {
		Random random = new Random();
		return random.nextInt(10);
	}

	public boolean verifyConfirmationCode(TransactionDb receivedTransaction, Integer referenceCode) {
		return receivedTransaction.getConfirmationCode().equals(referenceCode);
	}

	public void expiryTimer(TransactionDb transaction) {
		expiryFunctionality.setTransaction(transaction);
		expiryFunctionality.start();
	}
}
