package com.project.banking.services;

import com.project.banking.models.*;

import java.util.Optional;

public interface TransactionsService {
	public Optional<Transaction> findById(int id);

	public Transaction saveTransaction(Transaction transaction);

	public Transaction update(Transaction transaction);

	Transaction fillAndSave(TransactionIncoming transactionIncoming);

	public Transaction updateTransactionStatus(Transaction transaction, TransactionStatus newStatus);

	public Integer generateAndSaveCode(Transaction transaction);

	public void saveConfirmationCode(int id, Integer code);

	public Integer getConfirmationCode(Transaction transaction);

	public ResultOperationWithTransaction createTransaction(TransactionIncoming transactionIncoming);

	public ResultOperationWithTransaction finaliseTransaction(Transaction transaction);

	void sendExpiredTransaction(Transaction transaction);
}
