package com.project.banking.controllers;

import com.fasterxml.jackson.annotation.JsonView;
import com.project.banking.models.Transaction;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Logger;

@RestController
@RequestMapping("/api/transaction")
public class TransactionAPI {

//	@GetMapping(produces = "application/json")
//	@JsonView()
//	public String test() {
//		return "Hello world";
//	}

	@PostMapping("/pay")
	public Transaction makeTransaction(@RequestBody Transaction transaction) {
		Logger.getGlobal().info(transaction.toString());
		return transaction;
	}
}
