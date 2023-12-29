package com.project.banking.controllers;

import com.project.banking.dao.AccountDAO;
import com.project.banking.models.Transaction;
import com.project.banking.models.TypeOfTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/transaction")
public class TransactionAPI {

	private final AccountDAO accountDAO;

	@Autowired
	public TransactionAPI(AccountDAO accountDAO) {
		this.accountDAO = accountDAO;
	}

//	@GetMapping(produces = "application/json")
//	@JsonView()
//	public String test() {
//		return "Hello world";
//	}

	@PostMapping("/pay")
	public Transaction makeTransaction(@RequestBody Transaction transaction) {
		Logger.getGlobal().info(transaction.toString());
		transaction.setTime(new Date());
		transaction.setTypeOfTransaction(TypeOfTransaction.TRANSFER);
		transaction.setSendingBank(1);
		accountDAO.transfer(transaction);
		return transaction;
	}
}
