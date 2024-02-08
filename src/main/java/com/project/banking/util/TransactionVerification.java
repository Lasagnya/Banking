package com.project.banking.util;

import com.project.banking.dao.AccountDAO;
import com.project.banking.dao.BankDAO;
import com.project.banking.model.database.Account;
import com.project.banking.model.TransactionIncoming;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransactionVerification {
	private final BankDAO bankDAO;
	private final AccountDAO accountDAO;

	@Autowired
	public TransactionVerification(BankDAO bankDAO, AccountDAO accountDAO) {
		this.bankDAO = bankDAO;
		this.accountDAO = accountDAO;
	}

	/**
	 * @return returns integer value, where each digit of the number means
	 * 			the result of the verification from 0 to 10. The first digit
	 * 			is receivingBank, the second is receivingAccount, the third
	 * 			is sendingAccount, the forth is amount of transaction.
	 * */
	public int verify(TransactionIncoming transaction) {
		int errors = isBankCorrect(transaction.getReceivingBank()) +
				isReceivingAccountCorrect(transaction.getReceivingAccount()) +
				isSendingAccountCorrect(transaction.getSendingAccount());
		if (errors == 0)
			errors += isAmountCorrect(accountDAO.findById(transaction.getSendingAccount()).get(), transaction.getAmount());
		return errors;
	}

	private int isBankCorrect(int id) {
		if (bankDAO.findById(id).isPresent())
			return 0;
		else return 1 * 1;
	}

	private int isReceivingAccountCorrect(int id) {
		if (accountDAO.findById(id).isPresent())
			return 0;
		else return 1 * 10;
	}

	private int isSendingAccountCorrect(int id) {
		if (accountDAO.findById(id).isPresent())
			return 0;
		else return 1 * 100;
	}

	private int isAmountCorrect(Account account, double amount) {
		if (account.getBalance() >= amount)
			return 0;
		else return 1 * 1000;
	}
}
