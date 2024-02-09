package com.project.banking.util;

import com.project.banking.model.database.Account;
import com.project.banking.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Класс для проверки потребности начисления процентов
 */

@Component
public class IsPercentsNeeded implements Runnable {
	private final AccountService accountService;

	@Autowired
	public IsPercentsNeeded(AccountService accountService) {
		this.accountService = accountService;
	}

	@Override
	@Transactional
	public void run() {
		List<Account> accounts = accountService.findByBank(1);
		accounts.forEach(account -> account.setPercents(account.getBalance() > 0));
	}
}
