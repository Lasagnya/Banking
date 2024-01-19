package com.project.banking.functions;

import com.project.banking.dao.AccountDAO;
import com.project.banking.dao.BankDAO;
import com.project.banking.dao.TransactionDAO;
import com.project.banking.models.Account;
import com.project.banking.models.Transaction;
import com.project.banking.models.TransactionIncoming;

public class TransactionVerification {
	private final static TransactionDAO transactionDAO = new TransactionDAO();
	private final static BankDAO bankDAO = new BankDAO();
	private final static AccountDAO accountDAO = new AccountDAO();

	public static int verify(TransactionIncoming transaction) {
		int errors = isBankCorrect(transaction.getReceivingBank()) +
				isReceivingAccountCorrect(transaction.getReceivingAccount()) +
				isSendingAccountCorrect(transaction.getSendingAccount());
		if (errors == 0)
			errors += isAmountCorrect(accountDAO.findById(transaction.getSendingAccount()).get(), transaction.getAmount());
		return errors;
	}

	private static int isBankCorrect(int id) {
		if (bankDAO.findById(id).isPresent())
			return 0;
		else return 1;
	}

	private static int isReceivingAccountCorrect(int id) {
		if (accountDAO.findById(id).isPresent())
			return 0;
		else return 10;
	}

	private static int isSendingAccountCorrect(int id) {
		if (accountDAO.findById(id).isPresent())
			return 0;
		else return 100;
	}

	private static int isAmountCorrect(Account account, double amount) {
		if (account.getBalance() >= amount)
			return 0;
		else return 1000;
	}
}
