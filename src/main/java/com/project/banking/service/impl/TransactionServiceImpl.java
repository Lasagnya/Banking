package com.project.banking.service.impl;

import com.project.banking.client.CallbackClient;
import com.project.banking.enumeration.Period;
import com.project.banking.enumeration.TransactionStatus;
import com.project.banking.domain.Account;
import com.project.banking.domain.Transaction;
import com.project.banking.exception.*;
import com.project.banking.repository.TransactionRepository;
import com.project.banking.service.AccountService;
import com.project.banking.service.TransactionService;
import com.project.banking.to.client.Callback;
import com.project.banking.to.client.TransactionIncoming;
import com.project.banking.to.front.OngoingTransaction;
import com.project.banking.util.ConfirmationCodeFunctionality;
import com.project.banking.util.TransactionVerification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebInputException;

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
	private final AccountService accountService;
	private final CallbackClient callbackClient;
	private final TransactionVerification transactionVerification;

	@Autowired
	public TransactionServiceImpl(TransactionRepository transactionRepository, @Lazy ConfirmationCodeFunctionality confirmationCode, AccountService accountService, CallbackClient callbackClient, TransactionVerification transactionVerification) {
		this.transactionRepository = transactionRepository;
		this.confirmationCode = confirmationCode;
		this.accountService = accountService;
		this.callbackClient = callbackClient;
		this.transactionVerification = transactionVerification;
	}

	@Override
	public Optional<Transaction> findById(int id) {
		return transactionRepository.findById(id);
	}

	@Override
	public Transaction saveTransaction(Transaction transaction) {
		return transactionRepository.save(transaction);
	}

	@Override
	public Transaction update(Transaction transaction) {
		return transactionRepository.save(transaction);
	}

	@Override
	public Transaction fillAndSave(TransactionIncoming transactionIncoming) {
		Transaction transaction = new Transaction(transactionIncoming);
		return saveTransaction(transaction);
	}

	@Override
	@Transactional
	public Transaction updateTransactionStatus(Transaction transaction, TransactionStatus newStatus) {
		transaction.setStatus(newStatus);
		transactionRepository.save(transaction);
		return transaction;
	}

	@Override
	@Transactional
	public Transaction generateAndSaveCode(Transaction transaction) {
		Integer code = confirmationCode.generateConfirmationCode(transaction);
		transaction.setConfirmationCode(code);
//		saveConfirmationCode(transaction.getId(), code);
		return transaction;
	}

	@Override
	@Transactional
	public void saveConfirmationCode(int id, Integer code) {
		findById(id).ifPresent(transaction -> {
			transaction.setConfirmationCode(code);
			this.update(transaction);
		});
	}

	public Integer getConfirmationCode(int id) {
		return findById(id).map(Transaction::getConfirmationCode).orElse(null);
	}

	@Override
	@Transactional
	public ResponseEntity<Callback> createTransaction(TransactionIncoming transactionIncoming) {
		try {
			transactionVerification.verify(transactionIncoming);
		} catch (IncorrectBankException | IncorrectReceivingAccountException | IncorrectSendingAccountException e) {
			return new ResponseEntity<>(Callback.generateInvalidCallback(transactionIncoming), HttpStatus.CONFLICT);
		} catch (IncorrectAmountException e) {
			Transaction transaction = fillAndSave(transactionIncoming);
			transaction = updateTransactionStatus(transaction, TransactionStatus.INVALID);
			return new ResponseEntity<>(new Callback(transaction, transaction.getClientInformation()), HttpStatus.CONFLICT);
		} catch (TransactionVerificationException e) {
			throw new RuntimeException(e);
		}
		Transaction transaction = fillAndSave(transactionIncoming);
		transaction = generateAndSaveCode(transaction);
		confirmationCode.expiryTimer(transaction);
		return new ResponseEntity<>(new Callback(transaction, transaction.getClientInformation()), HttpStatus.OK);
	}

	@Override
	public void sendExpiredTransaction(Transaction transaction) {
		Callback callback = new Callback(transaction, transaction.getClientInformation());
		callbackClient.sendTransaction(callback);
	}

	@Override
	public List<Transaction> getTransactionsByAccountForPeriod(Account account, Period period) {
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
	@Transactional
	public ResponseEntity<OngoingTransaction> finaliseTransaction(Transaction transaction) throws ResponseStatusException {
		Optional<Transaction> originalTransaction = findById(transaction.getId());
		if (originalTransaction.isPresent()) {																				// Если транзакция существует
			if (originalTransaction.get().getStatus() == TransactionStatus.EXPIRED) {										// Если просрочена — возвращаем клиенту статус просрочена
				throw new ServerWebInputException("Transaction has already expired");
			}
			if (originalTransaction.get().getStatus() == TransactionStatus.PENDING) {										// Если статус ожидания подтверждения, то:
				if (confirmationCode.verifyConfirmationCode(transaction, originalTransaction.get().getConfirmationCode())) {// проверяем пришедший код
					accountService.transfer(originalTransaction.get());														// при корректности переводим средства
					transaction = updateTransactionStatus(originalTransaction.get(), TransactionStatus.PAID);				// обновляем статус на PAID
					Callback callback = new Callback(transaction, transaction.getClientInformation());
					callbackClient.sendTransaction(callback);
					return new ResponseEntity<>(new OngoingTransaction(transaction), HttpStatus.OK);
				} else {
					throw new ServerWebInputException("Incorrect code");
				}
			}
			throw new ServerWebInputException("Transaction has already been completed");
		} else
			throw new ServerWebInputException("Transaction not found, incorrect id");
	}
}
