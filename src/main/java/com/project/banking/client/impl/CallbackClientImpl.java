package com.project.banking.client.impl;

import com.project.banking.to.client.Callback;
import com.project.banking.client.CallbackClient;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class CallbackClientImpl implements CallbackClient {
	@Override
	public RestClient getClientConnection(Callback callback) {
		return RestClient.create(callback.getCallbackUri());
	}

	@Override
	public void sendTransaction(Callback callback) {
		getClientConnection(callback).post().contentType(MediaType.APPLICATION_JSON).body(callback).retrieve();
	}
}
