package com.project.banking.functions;

import com.project.banking.dao.AccountDAO;
import com.project.banking.dao.BankDAO;
import com.project.banking.dao.TransactionDAO;
import com.project.banking.models.Account;
import com.project.banking.models.TransactionIncoming;

public class TransactionVerification {
	private final static BankDAO bankDAO = new BankDAO();
	private final static AccountDAO accountDAO = new AccountDAO();

	/**
	 * @return returns integer value, where each digit of the number means
	 * 			the result of the verification from 0 to 10. The first digit
	 * 			is receivingBank, the second is receivingAccount, the third
	 * 			is sendingAccount, the forth is amount of transaction.
	 * */
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
		else return 1 * 1;
	}

	private static int isReceivingAccountCorrect(int id) {
		if (accountDAO.findById(id).isPresent())
			return 0;
		else return 1 * 10;
	}

	private static int isSendingAccountCorrect(int id) {
		if (accountDAO.findById(id).isPresent())
			return 0;
		else return 1 * 100;
	}

	private static int isAmountCorrect(Account account, double amount) {
		if (account.getBalance() >= amount)
			return 0;
		else return 1 * 1000;
	}
}
