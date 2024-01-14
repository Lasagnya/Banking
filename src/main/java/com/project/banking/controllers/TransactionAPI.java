package com.project.banking.controllers;

import com.project.banking.dao.TransactionCallbackDAO;
import com.project.banking.dao.TransactionDAO;
import com.project.banking.functions.ConfirmationCodeFunctionality;
import com.project.banking.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;
import org.springframework.web.servlet.ModelAndView;

import java.util.Optional;
import java.util.logging.Logger;

@Controller
@RequestMapping("/api/transaction")
public class TransactionAPI {

	private final TransactionDAO transactionDAO;
	private final TransactionCallbackDAO transactionCallbackDAO;
	private final ConfirmationCodeFunctionality confirmationCode;

	@Autowired
	public TransactionAPI(TransactionDAO transactionDAO, TransactionCallbackDAO transactionCallbackDAO, ConfirmationCodeFunctionality confirmationCode) {
		this.transactionDAO = transactionDAO;
		this.transactionCallbackDAO = transactionCallbackDAO;
		this.confirmationCode = confirmationCode;
	}

//	@GetMapping(produces = "application/json")
//	@JsonView()
//	public String test() {
//		return "Hello world";
//	}

	public RestClient.RequestBodySpec getClientConnection(TransactionCallback transactionCallback) {
		RestClient restClient = RestClient.create(transactionCallback.getCallbackUri());
		return restClient.post().contentType(MediaType.APPLICATION_JSON);
	}

	@PostMapping(value = "/pay", produces = MediaType.TEXT_HTML_VALUE)
	public String makeTransaction(@RequestBody TransactionIncoming transactionIncoming, Model model) {
		Logger.getGlobal().info(transactionIncoming.toString());
		Transaction transaction = transactionDAO.fillAndSave(transactionIncoming);
		transactionDAO.generateAndSaveCode(transaction);
		transactionCallbackDAO.fillAndSave(transaction, transactionIncoming);
//		ModelAndView modelAndView = new ModelAndView();
//		modelAndView.addObject("transaction", transaction);
//		modelAndView.setViewName("confirm");
		model.addAttribute("transaction", transaction);
		return "confirm";
	}

	@PostMapping(value = "/confirming")
	public String finaliseTransaction(@RequestBody Transaction transaction, Model model) {
		Optional<Transaction> optionalTransaction = transactionDAO.findById(transaction.getId());
		if (optionalTransaction.isPresent()) {
			if (confirmationCode.verifyConfirmationCode(transaction, optionalTransaction.get().getConfirmationCode())) {
				transaction = transactionDAO.updateTransactionStatus(transaction, TransactionStatus.PAID);
				TransactionCallback transactionCallback = transactionCallbackDAO.findById(transaction.getId()).get();
				RestClient restClient = RestClient.create(transactionCallback.getCallbackUri());
				restClient.post().contentType(MediaType.APPLICATION_JSON).body(transactionCallback).retrieve();
				return "successful";
			} else {
				transaction = optionalTransaction.get();
				transaction.setConfirmationCode(null);
				model.addAttribute("transaction", transaction);
				return "confirm";
			}
		}
		else {
			transactionDAO.updateTransactionStatus(transaction, TransactionStatus.INVALID);
			return "error";
		}
	}
}
