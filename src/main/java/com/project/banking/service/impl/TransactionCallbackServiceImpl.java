package com.project.banking.service.impl;

import com.project.banking.to.client.Callback;
import com.project.banking.to.client.TransactionIncoming;
import com.project.banking.domain.ClientInformation;
import com.project.banking.domain.Transaction;
import com.project.banking.repository.TransactionCallbackRepository;
import com.project.banking.service.TransactionCallbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TransactionCallbackServiceImpl implements TransactionCallbackService {
	private final TransactionCallbackRepository transactionCallbackRepository;

	@Autowired
	public TransactionCallbackServiceImpl(TransactionCallbackRepository transactionCallbackRepository) {
		this.transactionCallbackRepository = transactionCallbackRepository;
	}

	@Override
	public void saveTransaction(ClientInformation transaction) {
		transactionCallbackRepository.save(transaction);
	}

	@Override
	public Callback fillAndSave(Transaction transaction, TransactionIncoming transactionIncoming) {
		Callback callback = new Callback(transaction, transactionIncoming);
		saveTransaction(new ClientInformation(callback));
		return callback;
	}

	@Override
	public Optional<Callback> findById(int id) {
		Optional<ClientInformation> transactionCallbackDb = transactionCallbackRepository.findById(id);
//		transactionCallbackDb.ifPresent(callbackDb -> Hibernate.initialize(callbackDb.getTransaction()));
		return transactionCallbackDb.map(callbackDb -> new Callback(callbackDb.getTransaction(), callbackDb));
	}

	@Override
	public Optional<Callback> findByTransactionId(int id) {
//		return transactionCallbackRepository.findByTransactionId(id);
		Optional<ClientInformation> transactionCallbackDb = transactionCallbackRepository.findByTransactionId(id);
//		transactionCallbackDb.ifPresent(callbackDb -> Hibernate.initialize(callbackDb.getTransaction()));
		return transactionCallbackDb.map(callbackDb -> new Callback(callbackDb.getTransaction(), callbackDb));
	}
}
