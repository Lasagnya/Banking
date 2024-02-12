package com.project.banking.util;

import com.project.banking.enumeration.TransactionStatus;
import com.project.banking.domain.Transaction;
import com.project.banking.service.TransactionService;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class ExpiryFunctionality extends Thread {
	private final TransactionService transactionService;
	@Setter
	private Transaction transaction;

	@Autowired
	public ExpiryFunctionality(TransactionService transactionService) {
		this.transactionService = transactionService;
	}

	@Override
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
