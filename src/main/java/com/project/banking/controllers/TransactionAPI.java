package com.project.banking.controllers;

import com.project.banking.dao.AccountDAO;
import com.project.banking.dao.TransactionCallbackDAO;
import com.project.banking.dao.TransactionDAO;
import com.project.banking.functions.ConfirmationCodeFunctionality;
import com.project.banking.functions.TransactionVerification;
import com.project.banking.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

import java.util.Optional;

@Controller
@RequestMapping("/api/transaction")
public class TransactionAPI {

	private final TransactionDAO transactionDAO;
	private final TransactionCallbackDAO transactionCallbackDAO;
	private final ConfirmationCodeFunctionality confirmationCode;
	private final AccountDAO accountDAO;

	@Autowired
	public TransactionAPI(TransactionDAO transactionDAO, TransactionCallbackDAO transactionCallbackDAO, ConfirmationCodeFunctionality confirmationCode, AccountDAO accountDAO) {
		this.transactionDAO = transactionDAO;
		this.transactionCallbackDAO = transactionCallbackDAO;
		this.confirmationCode = confirmationCode;
		this.accountDAO = accountDAO;
	}

	public static RestClient.RequestBodySpec getClientConnection(TransactionCallback transactionCallback) {
		RestClient restClient = RestClient.create(transactionCallback.getCallbackUri());
		return restClient.post().contentType(MediaType.APPLICATION_JSON);
	}

	@PostMapping(value = "/pay", produces = MediaType.TEXT_HTML_VALUE)
	public String makeTransaction(@RequestBody TransactionIncoming transactionIncoming, Model model) {
		int errors = TransactionVerification.verify(transactionIncoming);													// Верификация транзакции
		if (errors == 0) {																									// Если ошибок нет, то
			Transaction transaction = transactionDAO.fillAndSave(transactionIncoming);										// сохраняем транзакцию в бд
			transactionDAO.generateAndSaveCode(transaction);																// генерируем код подтверждения
			transactionCallbackDAO.fillAndSave(transaction, transactionIncoming);											// сохраняем callback в бд
			model.addAttribute("transaction", transaction);
			confirmationCode.expiryTimer(transaction);																		// задаём таймер просрочки подтверждения
			return "confirm";																								// посылаем пользователю страницу подтверждения
		}
		if (errors % 10 != 0 || (errors / 10) % 10 != 0 || (errors / 100) % 10 != 0) {										// Если ошибки с банком или счётом, то
			TransactionCallback transactionCallback = TransactionCallback.generateInvalidCallback(transactionIncoming);		// генерируем callback со статусом invalid
			getClientConnection(transactionCallback).body(transactionCallback).retrieve();									// отправляем клиенту
		}
		else if ((errors / 1000) % 10 != 0) {																				// если ошибка в сумме транзакции, то
			Transaction transaction = transactionDAO.fillAndSave(transactionIncoming);										// сохраняем транзакцию в бд
			transaction = transactionDAO.updateTransactionStatus(transaction, TransactionStatus.INVALID);					// обновляем статус транзакции на invalid
			transactionCallbackDAO.fillAndSave(transaction, transactionIncoming);											// сохраняем callback в бд
			TransactionCallback transactionCallback = transactionCallbackDAO.findById(transaction.getId()).get();
			getClientConnection(transactionCallback).body(transactionCallback).retrieve();									// отправляем клиенту callback
		}
		return "error";																										// отправляем пользователю страницу с ошибкой
	}

	@PostMapping(value = "/confirming")
	public String finaliseTransaction(@ModelAttribute("transaction") Transaction transaction, Model model) {
		Optional<Transaction> optionalTransaction = transactionDAO.findById(transaction.getId());
		if (optionalTransaction.isPresent()) {																				// Если транзакция существует
			if (optionalTransaction.get().getStatus() == TransactionStatus.EXPIRED) {										// Если просрочена — возвращаем клиенту статус просрочена
				return "expired";
			}
			if (optionalTransaction.get().getStatus() == TransactionStatus.PENDING) {										// Если статус ожидания подтверждения, то:
				if (confirmationCode.verifyConfirmationCode(transaction, optionalTransaction.get().getConfirmationCode())) {// проверяем пришедший код
					accountDAO.transfer(optionalTransaction.get());															// при корректности переводим средства
					transaction = transactionDAO.updateTransactionStatus(transaction, TransactionStatus.PAID);				// обновляем статус на PAID
					TransactionCallback transactionCallback = transactionCallbackDAO.findById(transaction.getId()).get();	// и посылаем клиенту новый статус
					getClientConnection(transactionCallback).body(transactionCallback).retrieve();
					return "successful";
				} else {																									// если нет — возвращаем снова страницу ввода кода
					transaction = optionalTransaction.get();
					transaction.setConfirmationCode(null);
					model.addAttribute("transaction", transaction);
					return "confirm";
				}
			}
			else return "error";																							// если любой иной статус транзакции — возвращаем пользователю ошибку
		}
		else return "error";																								// если транзакция не найдена — возвращаем ошибку
	}
}
