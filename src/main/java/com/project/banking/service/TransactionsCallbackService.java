package com.project.banking.service;

import com.project.banking.model.Transaction;
import com.project.banking.model.TransactionCallback;
import com.project.banking.model.TransactionIncoming;

import java.util.Optional;

public interface TransactionsCallbackService {
	void saveTransaction(TransactionCallback transaction);

	TransactionCallback fillAndSave(Transaction transaction, TransactionIncoming transactionIncoming);

	Optional<TransactionCallback> findById(int id);
}
