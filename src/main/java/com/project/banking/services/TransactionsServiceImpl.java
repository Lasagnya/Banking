package com.project.banking.services;

import com.project.banking.dao.AccountDAO;
import com.project.banking.dao.TransactionCallbackDAO;
import com.project.banking.dao.TransactionDAO;
import com.project.banking.functions.ConfirmationCodeFunctionality;
import com.project.banking.functions.TransactionVerification;
import com.project.banking.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TransactionsServiceImpl implements TransactionsService {
	private final TransactionDAO transactionDAO;
	private final ConfirmationCodeFunctionality confirmationCode;
	private final TransactionCallbackDAO transactionCallbackDAO;
	private final AccountDAO accountDAO;
	private final CallbackClient callbackClient;

	@Autowired
	public TransactionsServiceImpl(TransactionDAO transactionDAO, ConfirmationCodeFunctionality confirmationCodeFunctionality, TransactionCallbackDAO transactionCallbackDAO, AccountDAO accountDAO, CallbackClient callbackClient) {
		this.transactionDAO = transactionDAO;
		this.confirmationCode = confirmationCodeFunctionality;
		this.transactionCallbackDAO = transactionCallbackDAO;
		this.accountDAO = accountDAO;
		this.callbackClient = callbackClient;
	}

	@Override
	public Optional<Transaction> findById(int id) {
		return transactionDAO.findById(id);
	}

	@Override
	public Transaction saveTransaction(Transaction transaction) {
		return transactionDAO.saveTransaction(transaction);
	}

	@Override
	public Transaction update(Transaction transaction) {
		return transactionDAO.update(transaction);
	}

	@Override
	public Transaction fillAndSave(TransactionIncoming transactionIncoming) {
		Transaction transaction = new Transaction(transactionIncoming);
		return transactionDAO.saveTransaction(transaction);
	}

	@Override
	public Transaction updateTransactionStatus(Transaction transaction, TransactionStatus newStatus) {
		return transactionDAO.updateTransactionStatus(transaction, newStatus);
	}

	@Override
	public Integer generateAndSaveCode(Transaction transaction) {
		Integer code = confirmationCode.generateConfirmationCode(transaction);
		saveConfirmationCode(transaction.getId(), code);
		return code;
	}

	@Override
	public void saveConfirmationCode(int id, Integer code) {
		transactionDAO.saveConfirmationCode(id, code);
	}

	public Integer getConfirmationCode(Transaction transaction) {
		return transactionDAO.getConfirmationCode(transaction);
	}

	@Override
	public TransactionCallback createTransaction(TransactionIncoming transactionIncoming) {
		int errors = TransactionVerification.verify(transactionIncoming);													// Верификация транзакции
		if (errors == 0) {																									// Если ошибок нет, то
			Transaction transaction = fillAndSave(transactionIncoming);										// сохраняем транзакцию в бд
			generateAndSaveCode(transaction);																// генерируем код подтверждения
			confirmationCode.expiryTimer(transaction);
			return transactionCallbackDAO.fillAndSave(transaction, transactionIncoming);											// сохраняем callback в бд
		}
		else if ((errors / 1000) % 10 != 0) {																				// если ошибка в сумме транзакции, то
			Transaction transaction = fillAndSave(transactionIncoming);										// сохраняем транзакцию в бд
			transaction = updateTransactionStatus(transaction, TransactionStatus.INVALID);					// обновляем статус транзакции на invalid
			return transactionCallbackDAO.fillAndSave(transaction, transactionIncoming);									// сохраняем callback в бд
		}
		else {																				// Если ошибки с банком или счётом, то
			return TransactionCallback.generateInvalidCallback(transactionIncoming);		// генерируем callback со статусом invalid
		}
	}

	public void sendExpiredTransaction(Transaction transaction) {
		TransactionCallback transactionCallback = transactionCallbackDAO.findById(transaction.getId()).get();
		callbackClient.sendTransaction(transactionCallback);
	}

	@Override
	public FinalisingTransactionResult finaliseTransaction(Transaction transaction) {
		Optional<Transaction> optionalTransaction = findById(transaction.getId());
		if (optionalTransaction.isPresent()) {																				// Если транзакция существует
			if (optionalTransaction.get().getStatus() == TransactionStatus.EXPIRED) {										// Если просрочена — возвращаем клиенту статус просрочена
				return new FinalisingTransactionResult(transaction, new ApiError(3));
			}
			if (optionalTransaction.get().getStatus() == TransactionStatus.PENDING) {										// Если статус ожидания подтверждения, то:
				if (confirmationCode.verifyConfirmationCode(transaction, optionalTransaction.get().getConfirmationCode())) {// проверяем пришедший код
					accountDAO.transfer(optionalTransaction.get());															// при корректности переводим средства
					transaction = updateTransactionStatus(transaction, TransactionStatus.PAID);								// обновляем статус на PAID
					TransactionCallback transactionCallback = transactionCallbackDAO.findById(transaction.getId()).get();	// и посылаем клиенту новый статус
					callbackClient.sendTransaction(transactionCallback);
					return new FinalisingTransactionResult(transaction, new ApiError(0));
				} else {
					return new FinalisingTransactionResult(transaction, new ApiError(2));
				}
			}
			return new FinalisingTransactionResult(transaction, new ApiError(4));									// если любой иной статус транзакции — возвращаем пользователю ошибку
		}
		return new FinalisingTransactionResult(transaction, new ApiError(1));										// если транзакция не найдена — возвращаем ошибку
	}
}
