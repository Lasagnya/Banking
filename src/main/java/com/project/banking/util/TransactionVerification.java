package com.project.banking.util;

import com.project.banking.domain.Account;
import com.project.banking.to.client.TransactionIncoming;
import com.project.banking.service.AccountService;
import com.project.banking.service.BankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransactionVerification {
	private final BankService bankService;
	private final AccountService accountService;

	@Autowired
	public TransactionVerification(BankService bankService, AccountService accountService) {
		this.bankService = bankService;
		this.accountService = accountService;
	}

	/**
	 * @return returns integer value, where each digit of the number means
	 * 			the result of the verification from 0 to 10. The first digit
	 * 			is receivingBank, the second is receivingAccount, the third
	 * 			is sendingAccount, the forth is amount of transaction.
	 * */
	public int verify(TransactionIncoming transaction) {																	// TODO по идее, так возвращать не очень красиво
		int errors = isBankCorrect(transaction.getReceivingBank()) +
				isReceivingAccountCorrect(transaction.getReceivingAccount()) +
				isSendingAccountCorrect(transaction.getSendingAccount());
		if (errors == 0)
			errors += isAmountCorrect(accountService.findById(transaction.getSendingAccount()).get(), transaction.getAmount());
		return errors;
	}

	private int isBankCorrect(int id) {
		if (bankService.findById(id).isPresent())
			return 0;
		else return 1 * 1;
	}

	private int isReceivingAccountCorrect(int id) {
		if (accountService.findById(id).isPresent())
			return 0;
		else return 1 * 10;
	}

	private int isSendingAccountCorrect(int id) {
		if (accountService.findById(id).isPresent())
			return 0;
		else return 1 * 100;
	}

	private int isAmountCorrect(Account account, double amount) {
		if (account.getBalance() >= amount)
			return 0;
		else return 1 * 1000;
	}
}
