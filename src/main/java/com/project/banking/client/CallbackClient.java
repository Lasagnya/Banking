package com.project.banking.client;

import com.project.banking.model.TransactionCallback;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public interface CallbackClient {
	RestClient getClientConnection(TransactionCallback transactionCallback);

	void sendTransaction(TransactionCallback transaction);
}
