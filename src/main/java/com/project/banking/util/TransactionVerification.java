package com.project.banking.util;

import com.project.banking.domain.Account;
import com.project.banking.exception.*;
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

	public void verify(TransactionIncoming transaction) throws TransactionVerificationException {
		isBankCorrect(transaction.getReceivingBank());
		isReceivingAccountCorrect(transaction.getReceivingAccount());
		isSendingAccountCorrect(transaction.getSendingAccount());
		isAmountCorrect(accountService.findById(transaction.getSendingAccount()).get(), transaction.getAmount());
	}

	private void isBankCorrect(int id) throws IncorrectBankException {
		if (bankService.findById(id).isEmpty())
			throw new IncorrectBankException();
	}

	private void isReceivingAccountCorrect(int id) throws IncorrectReceivingAccountException {
		if (accountService.findById(id).isEmpty())
			throw new IncorrectReceivingAccountException();
	}

	private void isSendingAccountCorrect(int id) throws IncorrectSendingAccountException {
		if (accountService.findById(id).isEmpty())
			throw new IncorrectSendingAccountException();
	}

	private void isAmountCorrect(Account account, double amount) throws IncorrectAmountException {
		if (account.getBalance() < amount)
			throw new IncorrectAmountException();
	}
}
