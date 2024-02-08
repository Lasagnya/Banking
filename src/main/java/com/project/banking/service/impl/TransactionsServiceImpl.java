package com.project.banking.service.impl;

import com.project.banking.dao.AccountDAO;
import com.project.banking.dao.TransactionDAO;
import com.project.banking.client.CallbackClient;
import com.project.banking.enumeration.TransactionStatus;
import com.project.banking.model.Transaction;
import com.project.banking.model.database.TransactionDb;
import com.project.banking.service.TransactionsCallbackService;
import com.project.banking.service.TransactionsService;
import com.project.banking.util.ConfirmationCodeFunctionality;
import com.project.banking.util.TransactionVerification;
import com.project.banking.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TransactionsServiceImpl implements TransactionsService {
	private final TransactionDAO transactionDAO;
	private ConfirmationCodeFunctionality confirmationCode;
	private final TransactionsCallbackService transactionsCallbackService;
	private final AccountDAO accountDAO;
	private final CallbackClient callbackClient;
	private final TransactionVerification transactionVerification;

	@Autowired
	public TransactionsServiceImpl(TransactionDAO transactionDAO, @Lazy ConfirmationCodeFunctionality confirmationCode, TransactionsCallbackService transactionsCallbackService, AccountDAO accountDAO, CallbackClient callbackClient, TransactionVerification transactionVerification) {
		this.transactionDAO = transactionDAO;
		this.confirmationCode = confirmationCode;
		this.transactionsCallbackService = transactionsCallbackService;
		this.accountDAO = accountDAO;
		this.callbackClient = callbackClient;
		this.transactionVerification = transactionVerification;
	}

	@Override
	public Optional<TransactionDb> findById(int id) {
		return transactionDAO.findById(id);
	}

	@Override
	public TransactionDb saveTransaction(TransactionDb transaction) {
		return transactionDAO.saveTransaction(transaction);
	}

	@Override
	public TransactionDb update(TransactionDb transaction) {
		return transactionDAO.update(transaction);
	}

	@Override
	public TransactionDb fillAndSave(TransactionIncoming transactionIncoming) {
		TransactionDb transaction = new TransactionDb(transactionIncoming);
		return transactionDAO.saveTransaction(transaction);
	}

	@Override
	public TransactionDb updateTransactionStatus(TransactionDb transaction, TransactionStatus newStatus) {
		transactionDAO.updateTransactionStatus(transaction, newStatus);
		transaction.setStatus(newStatus);
		return transaction;
	}

	@Override
	public Integer generateAndSaveCode(TransactionDb transaction) {
		Integer code = confirmationCode.generateConfirmationCode(transaction);
		saveConfirmationCode(transaction.getId(), code);
		return code;
	}

	@Override
	public void saveConfirmationCode(int id, Integer code) {
		transactionDAO.saveConfirmationCode(id, code);
	}

	public Integer getConfirmationCode(int id) {
		return transactionDAO.getConfirmationCode(id);
	}

	@Override
	public TransactionCallback createTransaction(TransactionIncoming transactionIncoming) {
		int errors = transactionVerification.verify(transactionIncoming);													// Верификация транзакции
		if (errors == 0) {																									// Если ошибок нет, то
			TransactionDb transaction = fillAndSave(transactionIncoming);														// сохраняем транзакцию в бд
			generateAndSaveCode(transaction);																				// генерируем код подтверждения
			confirmationCode.expiryTimer(transaction);
			return transactionsCallbackService.fillAndSave(transaction, transactionIncoming);								// сохраняем callback в бд
		}
		else if ((errors / 1000) % 10 != 0) {																				// если ошибка в сумме транзакции, то
			TransactionDb transaction = fillAndSave(transactionIncoming);														// сохраняем транзакцию в бд
			transaction = updateTransactionStatus(transaction, TransactionStatus.INVALID);									// обновляем статус транзакции на invalid
			return transactionsCallbackService.fillAndSave(transaction, transactionIncoming);								// сохраняем callback в бд
		}
		else {																												// Если ошибки с банком или счётом, то
			return TransactionCallback.generateInvalidCallback(transactionIncoming);										// генерируем callback со статусом invalid
		}
	}

	@Override
	public void sendExpiredTransaction(TransactionDb transaction) {
		TransactionCallback transactionCallback = transactionsCallbackService.findById(transaction.getId()).get();
		callbackClient.sendTransaction(transactionCallback);
	}

	@Override
	public FinalisingTransactionResult finaliseTransaction(TransactionDb transaction) {
		Optional<TransactionDb> optionalTransaction = findById(transaction.getId());
		if (optionalTransaction.isPresent()) {																				// Если транзакция существует
			if (optionalTransaction.get().getStatus() == TransactionStatus.EXPIRED) {										// Если просрочена — возвращаем клиенту статус просрочена
				return new FinalisingTransactionResult(new Transaction(transaction), new ApiError(3));
			}
			if (optionalTransaction.get().getStatus() == TransactionStatus.PENDING) {										// Если статус ожидания подтверждения, то:
				if (confirmationCode.verifyConfirmationCode(transaction, optionalTransaction.get().getConfirmationCode())) {// проверяем пришедший код
					accountDAO.transfer(optionalTransaction.get());															// при корректности переводим средства
					transaction = updateTransactionStatus(transaction, TransactionStatus.PAID);								// обновляем статус на PAID
					TransactionCallback transactionCallback = transactionsCallbackService.findById(transaction.getId()).get();	// и посылаем клиенту новый статус
					callbackClient.sendTransaction(transactionCallback);
					return new FinalisingTransactionResult(new Transaction(transaction), new ApiError(0));
				} else {
					return new FinalisingTransactionResult(new Transaction(transaction), new ApiError(2));
				}
			}
			return new FinalisingTransactionResult(new Transaction(transaction), new ApiError(4));									// если любой иной статус транзакции — возвращаем пользователю ошибку
		}
		return new FinalisingTransactionResult(new Transaction(transaction), new ApiError(1));										// если транзакция не найдена — возвращаем ошибку
	}
}
