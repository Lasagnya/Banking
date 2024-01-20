package com.project.banking.functions;

import com.project.banking.controllers.TransactionAPI;
import com.project.banking.dao.TransactionCallbackDAO;
import com.project.banking.dao.TransactionDAO;
import com.project.banking.models.Transaction;
import com.project.banking.models.TransactionCallback;
import com.project.banking.models.TransactionStatus;

import java.util.concurrent.TimeUnit;

public class ExpiryFunctionality extends Thread {
	private final TransactionDAO transactionDAO = new TransactionDAO();
	private final TransactionCallbackDAO callbackDAO = new TransactionCallbackDAO();
	private Transaction transaction;

	public ExpiryFunctionality(Transaction transaction) {
		this.transaction = transaction;
	}

	@Override
	public void run() {
		try {
			TimeUnit.MINUTES.sleep(2);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		transaction = transactionDAO.findById(transaction.getId()).get();
		if (transaction.getStatus() != TransactionStatus.PAID) {
			transactionDAO.updateTransactionStatus(transaction, TransactionStatus.EXPIRED);
			TransactionCallback transactionCallback = callbackDAO.findById(transaction.getId()).get();
			TransactionAPI.getClientConnection(transactionCallback).body(transactionCallback).retrieve();
		}
	}
}
