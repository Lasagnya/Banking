package com.project.banking.controllers;

import com.project.banking.models.*;
import com.project.banking.services.TransactionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transaction")
public class TransactionAPI {
	private final TransactionsService transactionsService;

	@Autowired
	public TransactionAPI(TransactionsService transactionsService) {
		this.transactionsService = transactionsService;
	}

	@PostMapping(value="/pay", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResultOperationWithTransaction makeTransaction(@RequestBody TransactionIncoming transactionIncoming) {
		return transactionsService.createTransaction(transactionIncoming);
	}

	@PostMapping(value = "/confirming")
	public ResultOperationWithTransaction finaliseTransaction(@RequestBody Transaction transaction) {
		return transactionsService.finaliseTransaction(transaction);
	}
}
