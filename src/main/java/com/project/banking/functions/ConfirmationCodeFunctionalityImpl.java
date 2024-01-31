package com.project.banking.functions;

import com.project.banking.models.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class ConfirmationCodeFunctionalityImpl implements ConfirmationCodeFunctionality {
	private final ExpiryFunctionality expiryFunctionality;

	@Autowired
	public ConfirmationCodeFunctionalityImpl(ExpiryFunctionality expiryFunctionality) {
		this.expiryFunctionality = expiryFunctionality;
	}

	@Override
	public Integer generateConfirmationCode(Transaction transaction) {
		Random random = new Random();
		return random.nextInt(10);
	}

	@Override
	public boolean verifyConfirmationCode(Transaction receivedTransaction, Integer referenceCode) {
		return receivedTransaction.getConfirmationCode().equals(referenceCode);
	}

	@Override
	public void expiryTimer(Transaction transaction) {
		expiryFunctionality.setTransaction(transaction);
		expiryFunctionality.start();
	}
}
