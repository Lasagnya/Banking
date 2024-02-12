package com.project.banking.controller;

import com.project.banking.domain.Transaction;
import com.project.banking.service.TransactionService;
import com.project.banking.to.client.Callback;
import com.project.banking.to.client.TransactionIncoming;
import com.project.banking.to.front.FinalisingTransactionResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transaction")
public class TransactionAPI {
	private final TransactionService transactionService;

	@Autowired
	public TransactionAPI(TransactionService transactionService) {
		this.transactionService = transactionService;
	}

	@PostMapping(value="/pay", produces = MediaType.APPLICATION_JSON_VALUE)
	public Callback makeTransaction(@RequestBody TransactionIncoming transactionIncoming) {					// посылает ответ на бек A
		return transactionService.createTransaction(transactionIncoming);
	}

	@PostMapping(value = "/confirming")
	public FinalisingTransactionResult finaliseTransaction(@RequestBody Transaction transaction) {					// посылает ответ на фронт B
		return transactionService.finaliseTransaction(transaction);
	}
}
