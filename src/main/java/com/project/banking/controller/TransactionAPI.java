package com.project.banking.controller;

import com.project.banking.model.*;
import com.project.banking.model.database.TransactionDb;
import com.project.banking.service.TransactionService;
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
	public TransactionCallback makeTransaction(@RequestBody TransactionIncoming transactionIncoming) {					// посылает ответ на бек A
		return transactionService.createTransaction(transactionIncoming);
	}

	@PostMapping(value = "/confirming")
	public FinalisingTransactionResult finaliseTransaction(@RequestBody TransactionDb transaction) {						// посылает ответ на фронт B
		return transactionService.finaliseTransaction(transaction);
	}
}
