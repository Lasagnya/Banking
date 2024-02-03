package com.project.banking.services;

import com.project.banking.models.*;

import java.util.Optional;

public interface TransactionsService {
	Optional<Transaction> findById(int id);

	Transaction saveTransaction(Transaction transaction);

	Transaction update(Transaction transaction);

	Transaction fillAndSave(TransactionIncoming transactionIncoming);

	Transaction updateTransactionStatus(Transaction transaction, TransactionStatus newStatus);

	Integer generateAndSaveCode(Transaction transaction);

	void saveConfirmationCode(int id, Integer code);

	Integer getConfirmationCode(Transaction transaction);

	TransactionCallback createTransaction(TransactionIncoming transactionIncoming);

	FinalisingTransactionResult finaliseTransaction(Transaction transaction);

	void sendExpiredTransaction(Transaction transaction);
}
