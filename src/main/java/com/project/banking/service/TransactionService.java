package com.project.banking.service;

import com.project.banking.enumeration.Period;
import com.project.banking.enumeration.TransactionStatus;
import com.project.banking.domain.Account;
import com.project.banking.domain.Transaction;
import com.project.banking.to.client.Callback;
import com.project.banking.to.client.TransactionIncoming;
import com.project.banking.to.front.FinalisingTransactionResult;

import java.util.List;
import java.util.Optional;

public interface TransactionService {
	Optional<Transaction> findById(int id);

	Transaction saveTransaction(Transaction transaction);

	Transaction update(Transaction transaction);

	Transaction fillAndSave(TransactionIncoming transactionIncoming);

	Transaction updateTransactionStatus(Transaction transaction, TransactionStatus newStatus);

	Transaction generateAndSaveCode(Transaction transaction);

	void saveConfirmationCode(int id, Integer code);

	Integer getConfirmationCode(int id);

	Callback createTransaction(TransactionIncoming transactionIncoming);

	FinalisingTransactionResult finaliseTransaction(Transaction transaction);

	void sendExpiredTransaction(Transaction transaction);

	/**
	 * Создание списка транзакций, связанных со счётом
	 * @param account ищутся транзакии, связанные с этим счётом
	 * @param period период, за который ищутся транзакции
	 * @return список найденных транзакций
	 */
	List<Transaction> getTransactionsByAccountForPeriod(Account account, Period period);
}
