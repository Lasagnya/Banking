package com.project.banking.util;

import com.project.banking.models.Transaction;

public interface ConfirmationCodeFunctionality {
	public Integer generateConfirmationCode(Transaction transaction);

	public boolean verifyConfirmationCode(Transaction receivedTransaction, Integer referenceCode);

	public void expiryTimer(Transaction transaction);
}
