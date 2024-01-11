package com.project.banking.controllers;

import com.project.banking.dao.AccountDAO;
import com.project.banking.models.Transaction;
import com.project.banking.models.TransactionIncoming;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

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

	@PostMapping(value = "/pay")
	public ModelAndView makeTransaction(@RequestBody TransactionIncoming transactionIncoming) {
		Logger.getGlobal().info(transactionIncoming.toString());
		Transaction transaction = new Transaction(transactionIncoming);
//		input.setTime(new Date());
//		input.setTypeOfTransaction(TypeOfTransaction.TRANSFER);
//		input.setSendingBank(1);		//по идее, банк устанавливает банкинг по номеру счёта
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("transaction", transaction);
		modelAndView.setViewName("confirm");
//		RestClient restClient = RestClient.create("http://localhost:8080");
//		String result = restClient.post().contentType(MediaType.TEXT_HTML).body(modelAndView).retrieve().body(String.class);
//		Logger.getGlobal().info(result);
		return modelAndView;
	}
}
