package com.project.banking.service;

import com.project.banking.model.TransactionCallback;
import com.project.banking.model.TransactionIncoming;
import com.project.banking.model.database.TransactionCallbackDb;
import com.project.banking.model.database.TransactionDb;

import java.util.Optional;

public interface TransactionCallbackService {
	void saveTransaction(TransactionCallbackDb transaction);

	TransactionCallback fillAndSave(TransactionDb transaction, TransactionIncoming transactionIncoming);

	Optional<TransactionCallback> findById(int id);
}
