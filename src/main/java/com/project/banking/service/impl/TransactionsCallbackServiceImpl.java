package com.project.banking.service.impl;

import com.project.banking.dao.TransactionCallbackDAO;
import com.project.banking.model.Transaction;
import com.project.banking.model.TransactionCallback;
import com.project.banking.model.TransactionIncoming;
import com.project.banking.enumeration.TransactionStatus;
import com.project.banking.model.database.TransactionCallbackDb;
import com.project.banking.model.database.TransactionDb;
import com.project.banking.service.TransactionsCallbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class TransactionsCallbackServiceImpl implements TransactionsCallbackService {
	private final TransactionCallbackDAO transactionCallbackDAO;

	@Autowired
	public TransactionsCallbackServiceImpl(TransactionCallbackDAO transactionCallbackDAO) {
		this.transactionCallbackDAO = transactionCallbackDAO;
	}

	@Override
	public void saveTransaction(TransactionCallbackDb transaction) {
		transactionCallbackDAO.saveTransaction(transaction);
	}

	@Override
	public TransactionCallback fillAndSave(TransactionDb transaction, TransactionIncoming transactionIncoming) {
		TransactionCallback transactionCallback = new TransactionCallback(transaction, transactionIncoming);
		saveTransaction(new TransactionCallbackDb(transactionCallback));
		return transactionCallback;
	}

	@Override
	public Optional<TransactionCallback> findById(int id) {
		return transactionCallbackDAO.findById(id);
	}


	public TransactionCallback generateInvalidCallback(TransactionIncoming transactionIncoming) {
		TransactionCallback transactionCallback = new TransactionCallback();
		transactionCallback.setId(0);
		transactionCallback.setTime(new Date());
		transactionCallback.setInvoiceId(transactionIncoming.getInvoiceId());
		transactionCallback.setStatus(TransactionStatus.INVALID);
		transactionCallback.setSendingBank(0);
		transactionCallback.setReceivingBank(transactionIncoming.getReceivingBank());
		transactionCallback.setSendingAccount(transactionIncoming.getSendingAccount());
		transactionCallback.setReceivingAccount(transactionIncoming.getReceivingAccount());
		transactionCallback.setAmount(transactionIncoming.getAmount());
		transactionCallback.setCurrency(transactionIncoming.getCurrency());
		transactionCallback.setCallbackUri(transactionIncoming.getCallbackUri());
		return transactionCallback;
	}
}
