package com.project.banking.functions;

import com.project.banking.models.Transaction;
import org.springframework.stereotype.Component;

@Component
public class ConfirmationCodeFunctionality {

	public Integer generateConfirmationCode(Transaction transaction) {
		return 5;
	}

	public boolean verifyConfirmationCode(Transaction receivedTransaction, Integer referenceCode) {
		return receivedTransaction.getConfirmationCode().equals(referenceCode);
	}
}
