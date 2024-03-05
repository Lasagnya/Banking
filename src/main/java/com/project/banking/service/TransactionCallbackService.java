package com.project.banking.service;

import com.project.banking.to.client.Callback;
import com.project.banking.to.client.TransactionIncoming;
import com.project.banking.domain.ClientInformation;
import com.project.banking.domain.Transaction;

import java.util.Optional;

public interface TransactionCallbackService {
	void saveTransaction(ClientInformation transaction);

	Callback fillAndSave(Transaction transaction, TransactionIncoming transactionIncoming);

	Optional<Callback> findById(int id);

	Optional<Callback> findByTransactionId(int id);
}
