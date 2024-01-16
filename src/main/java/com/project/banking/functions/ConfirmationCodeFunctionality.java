package com.project.banking.functions;

import com.project.banking.models.Transaction;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class ConfirmationCodeFunctionality {

	public Integer generateConfirmationCode(Transaction transaction) {
		Random random = new Random();
		return random.nextInt(10);
	}

	public boolean verifyConfirmationCode(Transaction receivedTransaction, Integer referenceCode) {
		return receivedTransaction.getConfirmationCode().equals(referenceCode);
	}
}
