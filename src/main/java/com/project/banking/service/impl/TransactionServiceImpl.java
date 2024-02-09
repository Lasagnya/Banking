package com.project.banking.service.impl;

import com.project.banking.client.CallbackClient;
import com.project.banking.enumeration.Period;
import com.project.banking.enumeration.TransactionStatus;
import com.project.banking.model.database.Account;
import com.project.banking.model.database.TransactionDb;
import com.project.banking.repository.TransactionRepository;
import com.project.banking.service.AccountService;
import com.project.banking.service.TransactionCallbackService;
import com.project.banking.service.TransactionService;
import com.project.banking.util.ConfirmationCodeFunctionality;
import com.project.banking.util.TransactionVerification;
import com.project.banking.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionServiceImpl implements TransactionService {
	private final TransactionRepository transactionRepository;
	private ConfirmationCodeFunctionality confirmationCode;
	private final TransactionCallbackService transactionCallbackService;
	private final AccountService accountService;
	private final CallbackClient callbackClient;
	private final TransactionVerification transactionVerification;

	@Autowired
	public TransactionServiceImpl(TransactionRepository transactionRepository, @Lazy ConfirmationCodeFunctionality confirmationCode, TransactionCallbackService transactionCallbackService, AccountService accountService, CallbackClient callbackClient, TransactionVerification transactionVerification) {
		this.transactionRepository = transactionRepository;
		this.confirmationCode = confirmationCode;
		this.transactionCallbackService = transactionCallbackService;
		this.accountService = accountService;
		this.callbackClient = callbackClient;
		this.transactionVerification = transactionVerification;
	}

	@Override
	public Optional<TransactionDb> findById(int id) {
		return transactionRepository.findById(id);
	}

	@Override
	public TransactionDb saveTransaction(TransactionDb transaction) {
		return transactionRepository.save(transaction);
	}

	@Override
	public TransactionDb update(TransactionDb transaction) {
		return transactionRepository.save(transaction);
	}

	@Override
	public TransactionDb fillAndSave(TransactionIncoming transactionIncoming) {
		TransactionDb transaction = new TransactionDb(transactionIncoming);
		return saveTransaction(transaction);
	}

	@Override
	public TransactionDb updateTransactionStatus(TransactionDb transaction, TransactionStatus newStatus) {
		transaction.setStatus(newStatus);
		transactionRepository.save(transaction);
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
		Optional<TransactionDb> transactionDb = findById(id);
		if (transactionDb.isPresent()) {
			transactionDb.get().setConfirmationCode(code);
			update(transactionDb.get());
		}
	}

	public Integer getConfirmationCode(int id) {
		return findById(id).map(TransactionDb::getConfirmationCode).orElse(null);
	}

	@Override
	public TransactionCallback createTransaction(TransactionIncoming transactionIncoming) {
		int errors = transactionVerification.verify(transactionIncoming);													// Верификация транзакции
		if (errors == 0) {																									// Если ошибок нет, то
			TransactionDb transaction = fillAndSave(transactionIncoming);														// сохраняем транзакцию в бд
			generateAndSaveCode(transaction);																				// генерируем код подтверждения
			confirmationCode.expiryTimer(transaction);
			return transactionCallbackService.fillAndSave(transaction, transactionIncoming);								// сохраняем callback в бд
		}
		else if ((errors / 1000) % 10 != 0) {																				// если ошибка в сумме транзакции, то
			TransactionDb transaction = fillAndSave(transactionIncoming);														// сохраняем транзакцию в бд
			transaction = updateTransactionStatus(transaction, TransactionStatus.INVALID);									// обновляем статус транзакции на invalid
			return transactionCallbackService.fillAndSave(transaction, transactionIncoming);								// сохраняем callback в бд
		}
		else {																												// Если ошибки с банком или счётом, то
			return TransactionCallback.generateInvalidCallback(transactionIncoming);										// генерируем callback со статусом invalid
		}
	}

	@Override
	public void sendExpiredTransaction(TransactionDb transaction) {
		TransactionCallback transactionCallback = transactionCallbackService.findById(transaction.getId()).get();
		callbackClient.sendTransaction(transactionCallback);
	}

	@Override
	public List<TransactionDb> getTransactionsByAccountForPeriod(Account account, Period period) {
		Date limit = Date.from(Instant.MIN);
		if (period == Period.MONTH) {
			limit = Date.from(LocalDate.now().minusMonths(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
		}
		else if (period == Period.YEAR) {
			limit = Date.from(LocalDate.now().minusYears(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
		}
		return transactionRepository.getTransactionsByAccountForPeriod(account.getId(), limit);
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
					accountService.transfer(optionalTransaction.get());															// при корректности переводим средства
					transaction = updateTransactionStatus(transaction, TransactionStatus.PAID);								// обновляем статус на PAID
					TransactionCallback transactionCallback = transactionCallbackService.findById(transaction.getId()).get();	// и посылаем клиенту новый статус
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
