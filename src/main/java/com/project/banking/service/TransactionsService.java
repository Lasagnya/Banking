package com.project.banking.service;

import com.project.banking.enumeration.TransactionStatus;
import com.project.banking.model.*;
import com.project.banking.model.Transaction;
import com.project.banking.model.database.TransactionDb;

import java.util.Optional;

public interface TransactionsService {
	Optional<TransactionDb> findById(int id);

	TransactionDb saveTransaction(TransactionDb transaction);

	TransactionDb update(TransactionDb transaction);

	TransactionDb fillAndSave(TransactionIncoming transactionIncoming);

	TransactionDb updateTransactionStatus(TransactionDb transaction, TransactionStatus newStatus);

	Integer generateAndSaveCode(TransactionDb transaction);

	void saveConfirmationCode(int id, Integer code);

	Integer getConfirmationCode(int id);

	TransactionCallback createTransaction(TransactionIncoming transactionIncoming);

	FinalisingTransactionResult finaliseTransaction(TransactionDb transaction);

	void sendExpiredTransaction(TransactionDb transaction);
}
