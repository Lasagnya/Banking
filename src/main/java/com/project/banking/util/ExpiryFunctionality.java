package com.project.banking.util;

import com.project.banking.enumeration.TransactionStatus;
import com.project.banking.domain.Transaction;
import com.project.banking.service.TransactionService;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@Scope("prototype")
public class ExpiryFunctionality {
	private final TransactionService transactionService;
	@Setter
	private Transaction transaction;

	@Autowired
	public ExpiryFunctionality(TransactionService transactionService) {
		this.transactionService = transactionService;
	}

	@Async
	public void run() {
		try {
			TimeUnit.MINUTES.sleep(2);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		transaction = transactionService.findById(transaction.getId()).get();
		if (transaction.getStatus() == TransactionStatus.PENDING || transaction.getStatus() == TransactionStatus.NEW) {
			transaction = transactionService.updateTransactionStatus(transaction, TransactionStatus.EXPIRED);
			transactionService.sendExpiredTransaction(transaction);
		}
	}
}
