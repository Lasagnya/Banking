package com.project.banking.service;

import com.project.banking.enumeration.Period;
import com.project.banking.enumeration.TransactionStatus;
import com.project.banking.model.*;
import com.project.banking.model.database.Account;
import com.project.banking.model.database.TransactionDb;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface TransactionService {
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

	/**
	 * Создание списка транзакций, связанных со счётом
	 * @param account ищутся транзакии, связанные с этим счётом
	 * @param period период, за который ищутся транзакции
	 * @return список найденных транзакций
	 */
	List<TransactionDb> getTransactionsByAccountForPeriod(Account account, Period period);
}
