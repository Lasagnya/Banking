package com.project.banking.functions;

import com.project.banking.models.Transaction;
import com.project.banking.models.TransactionStatus;
import com.project.banking.services.TransactionsService;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class ExpiryFunctionality extends Thread {
	private final TransactionsService transactionsService;
	@Setter
	private Transaction transaction;

	@Autowired
	public ExpiryFunctionality(TransactionsService transactionsService) {
		this.transactionsService = transactionsService;
	}

	@Override
	public void run() {
		try {
			TimeUnit.MINUTES.sleep(2);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		transaction = transactionsService.findById(transaction.getId()).get();
		if (transaction.getStatus() != TransactionStatus.PAID) {
			transactionsService.updateTransactionStatus(transaction, TransactionStatus.EXPIRED);
			transactionsService.sendExpiredTransaction(transaction);
		}
	}
}
