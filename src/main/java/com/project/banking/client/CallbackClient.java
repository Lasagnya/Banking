package com.project.banking.client;

import com.project.banking.to.client.Callback;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public interface CallbackClient {
	RestClient getClientConnection(Callback callback);

	void sendTransaction(Callback transaction);
}
