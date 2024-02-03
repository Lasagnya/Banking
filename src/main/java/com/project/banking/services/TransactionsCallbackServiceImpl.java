package com.project.banking.services;

import com.project.banking.dao.TransactionCallbackDAO;
import com.project.banking.models.Transaction;
import com.project.banking.models.TransactionCallback;
import com.project.banking.models.TransactionIncoming;
import com.project.banking.models.TransactionStatus;
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
	public void saveTransaction(TransactionCallback transaction) {
		transactionCallbackDAO.saveTransaction(transaction);
	}

	@Override
	public TransactionCallback fillAndSave(Transaction transaction, TransactionIncoming transactionIncoming) {
		TransactionCallback transactionCallback = new TransactionCallback(transaction, transactionIncoming);
		saveTransaction(transactionCallback);
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
