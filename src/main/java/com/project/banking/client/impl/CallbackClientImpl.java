package com.project.banking.client.impl;

import com.project.banking.model.TransactionCallback;
import com.project.banking.client.CallbackClient;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
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
