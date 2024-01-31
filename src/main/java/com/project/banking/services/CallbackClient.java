package com.project.banking.services;

import com.project.banking.models.TransactionCallback;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public interface CallbackClient {
	RestClient getClientConnection(TransactionCallback transactionCallback);

	void sendTransaction(TransactionCallback transaction);
}
