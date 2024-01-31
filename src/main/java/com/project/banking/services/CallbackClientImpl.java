package com.project.banking.services;

import com.project.banking.models.TransactionCallback;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

public class CallbackClientImpl implements CallbackClient {
	@Override
	public RestClient getClientConnection(TransactionCallback transactionCallback) {
		return RestClient.create(transactionCallback.getCallbackUri());
	}

	@Override
	public void sendTransaction(TransactionCallback transactionCallback) {
		getClientConnection(transactionCallback).post().contentType(MediaType.APPLICATION_JSON).body(transactionCallback).retrieve();
	}
}
