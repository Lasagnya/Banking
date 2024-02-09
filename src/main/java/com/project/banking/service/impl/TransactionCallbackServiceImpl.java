package com.project.banking.service.impl;

import com.project.banking.model.TransactionCallback;
import com.project.banking.model.TransactionIncoming;
import com.project.banking.enumeration.TransactionStatus;
import com.project.banking.model.database.TransactionCallbackDb;
import com.project.banking.model.database.TransactionDb;
import com.project.banking.repository.TransactionCallbackRepository;
import com.project.banking.service.TransactionCallbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class TransactionCallbackServiceImpl implements TransactionCallbackService {
	private final TransactionCallbackRepository transactionCallbackRepository;

	@Autowired
	public TransactionCallbackServiceImpl(TransactionCallbackRepository transactionCallbackRepository) {
		this.transactionCallbackRepository = transactionCallbackRepository;
	}

	@Override
	public void saveTransaction(TransactionCallbackDb transaction) {
		transactionCallbackRepository.save(transaction);
	}

	@Override
	public TransactionCallback fillAndSave(TransactionDb transaction, TransactionIncoming transactionIncoming) {
		TransactionCallback transactionCallback = new TransactionCallback(transaction, transactionIncoming);
		saveTransaction(new TransactionCallbackDb(transactionCallback));
		return transactionCallback;
	}

	@Override
	public Optional<TransactionCallback> findById(int id) {
		Optional<TransactionCallbackDb> transactionCallbackDb = transactionCallbackRepository.findById(id);
		return transactionCallbackDb.map(TransactionCallback::new);
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
