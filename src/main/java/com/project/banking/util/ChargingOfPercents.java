package com.project.banking.util;

import com.project.banking.model.database.Account;
import com.project.banking.service.AccountService;
import jakarta.persistence.Column;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

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
