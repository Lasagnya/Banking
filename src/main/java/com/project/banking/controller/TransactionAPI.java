package com.project.banking.controller;

import com.project.banking.domain.Transaction;
import com.project.banking.service.TransactionService;
import com.project.banking.to.client.Callback;
import com.project.banking.to.client.TransactionIncoming;
import com.project.banking.to.front.OngoingTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/transaction")
public class TransactionAPI {
	private final TransactionService transactionService;

	@Autowired
	public TransactionAPI(TransactionService transactionService) {
		this.transactionService = transactionService;
	}

	@PostMapping(value="/pay", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Callback> makeTransaction(@RequestBody TransactionIncoming transactionIncoming) {									// посылает ответ на бек A
		return transactionService.createTransaction(transactionIncoming);
	}

	@PostMapping(value = "/confirming", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<OngoingTransaction> finaliseTransaction(@RequestBody Transaction transaction) throws ResponseStatusException {	// посылает ответ на фронт B
		return transactionService.finaliseTransaction(transaction);
	}
}
