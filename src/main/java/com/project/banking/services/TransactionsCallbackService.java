package com.project.banking.services;

import com.project.banking.models.Transaction;
import com.project.banking.models.TransactionCallback;
import com.project.banking.models.TransactionIncoming;

import java.util.Optional;

public interface TransactionsCallbackService {
	void saveTransaction(TransactionCallback transaction);

	TransactionCallback fillAndSave(Transaction transaction, TransactionIncoming transactionIncoming);

	Optional<TransactionCallback> findById(int id);
}
