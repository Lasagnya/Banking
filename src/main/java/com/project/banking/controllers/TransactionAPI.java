package com.project.banking.controllers;

import com.project.banking.dao.AccountDAO;
import com.project.banking.dao.TransactionCallbackDAO;
import com.project.banking.dao.TransactionDAO;
import com.project.banking.models.Transaction;
import com.project.banking.models.TransactionCallback;
import com.project.banking.models.TransactionIncoming;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.logging.Logger;

@RestController
@RequestMapping("/api/transaction")
public class TransactionAPI {

	private final TransactionDAO transactionDAO;
	private final TransactionCallbackDAO transactionCallbackDAO;

	@Autowired
	public TransactionAPI(TransactionDAO transactionDAO, TransactionCallbackDAO transactionCallbackDAO) {
		this.transactionDAO = transactionDAO;
		this.transactionCallbackDAO = transactionCallbackDAO;
	}

//	@GetMapping(produces = "application/json")
//	@JsonView()
//	public String test() {
//		return "Hello world";
//	}

	@PostMapping(value = "/pay", produces = MediaType.TEXT_HTML_VALUE)
	public ModelAndView makeTransaction(@RequestBody TransactionIncoming transactionIncoming) {
		Logger.getGlobal().info(transactionIncoming.toString());
		Transaction transaction = transactionDAO.fillAndSave(transactionIncoming);
		transactionCallbackDAO.fillAndSave(transaction, transactionIncoming);
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("transaction", transaction);
		modelAndView.setViewName("confirm");
		return modelAndView;
	}
}
