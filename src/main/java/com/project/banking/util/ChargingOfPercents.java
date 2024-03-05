package com.project.banking.util;

import com.project.banking.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Класс для начисления процентов на счёт
 */

@Component
public class ChargingOfPercents implements Runnable {
	private final AccountService accountService;

	@Autowired
	public ChargingOfPercents(AccountService accountService) {
		this.accountService = accountService;
	}


	@Override
	@Transactional
	public void run() {
		accountService.chargePercents();
	}
}
