package com.project.banking.services;

import com.project.banking.dao.TransactionCallbackDAO;
import com.project.banking.models.Transaction;
import com.project.banking.models.TransactionCallback;
import com.project.banking.models.TransactionIncoming;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
		return transactionCallbackDAO.fillAndSave(transaction, transactionIncoming);
	}

	@Override
	public Optional<TransactionCallback> findById(int id) {
		return transactionCallbackDAO.findById(id);
	}
}
