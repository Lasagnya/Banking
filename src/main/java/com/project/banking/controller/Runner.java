package com.project.banking.controller;

import com.project.banking.enumeration.Currency;
import com.project.banking.enumeration.Period;
import com.project.banking.enumeration.TypeOfTransaction;
import com.project.banking.domain.Account;
import com.project.banking.domain.Bank;
import com.project.banking.domain.Transaction;
import com.project.banking.service.AccountService;
import com.project.banking.service.UserService;
import com.project.banking.util.SwitchInputMethods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Scanner;

/**
 *  Главный исполнительный файл с интерфейсом
 */
@Component
public class Runner {
	private final SwitchInputMethods sim;
	private final AccountService accountService;
	private final UserService userService;

	@Autowired
	public Runner(SwitchInputMethods sim, AccountService accountService, UserService userService) {
		this.sim = sim;
		this.accountService = accountService;
		this.userService = userService;
	}

	public void run() {
		Scanner scanner = new Scanner(System.in);

		userService.authentication();

		loop:
		while(true) {
			System.out.println();
			System.out.println(
					"""
							Банковская программа
							1: перевести средства на другой счёт
							2: снять деньги со счёта
							3: положить деньги на счёт
							4: сформировать выписку по счёту
							5: создать новый счёт
							6: изменить данные аутентификации
							0: выйти""");

			switch (scanner.nextInt()) {
				case 1: {
					System.out.println("Выберите банк-получатель:");
					int receivingBank = sim.getReceivingBank();
					System.out.println("Введите номер счёта получателя:");
					int receivingAccount = sim.getReceivingAccount(receivingBank);
					System.out.println("С какого счёта вы хотите перевести деньги?");
					Account sendingAccount = sim.getSendingAccount();
					System.out.println("Введите сумму, которую хотите перевести:");
					double amount = sim.getAmount(sendingAccount);

					Transaction transaction = new Transaction(new Date(), TypeOfTransaction.TRANSFER, 1, receivingBank, sendingAccount.getId(), receivingAccount, amount, Currency.BYN);
					accountService.transfer(transaction);
					break;
				}

				case 2: {
					System.out.println("С какого счёта вы хотите снять деньги?");
					Account sendingAccount = sim.getSendingAccount();
					System.out.println("Введите сумму, которую хотите снять:");
					double amount = sim.getAmount(sendingAccount);

					Transaction transaction = new Transaction(new Date(), TypeOfTransaction.WITHDRAWAL, 1, 1, sendingAccount.getId(), sendingAccount.getId(), amount, Currency.BYN);
					accountService.withdrawal(transaction);
					break;
				}

				case 3: {
					System.out.println("Выберите банк-получатель:");
					int receivingBank = sim.getReceivingBank();
					System.out.println("Введите номер счёта получателя:");
					int receivingAccount = sim.getReceivingAccount(receivingBank);
					System.out.println("Введите сумму, которую хотите перевести:");
					double amount = scanner.nextDouble();

					Transaction transaction = new Transaction(new Date(), TypeOfTransaction.PAYIN, 1, receivingBank, 1, receivingAccount, amount, Currency.BYN);
					accountService.payIn(transaction);
					break;
				}

				case 4: {
					System.out.println("По какому счёту хотите сформировать выписку?");
					Account account = sim.getSendingAccount();
					System.out.println("""
								За какой период формировать выписку?
								1: за месяц
								2: за год
								3: за всё время""");
					Period period = sim.getPeriod();
					System.out.println("""
								Вывести в txt или pdf?
								1: txt
								2: pdf""");
					int file = sim.getFileFormat();
					if (file == 1)
						accountService.excerpt(account, period);
					else if (file == 2)
						accountService.excerptInPDF(account, period);
					break;
				}

				case 5: {
					accountService.save(new Account(Currency.BYN, new Date(), new Bank(1), userService.getUser()));
					System.out.println("Счёт создан!");
					break;
				}

				case 6: {
					System.out.println("""
								Вы хотите изменить пароль или имя пользователя?
								1: пароль
								2: имя""");
					int value = sim.getFileFormat();
					if (value == 1)
						userService.changePassword();
					else if (value == 2)
						userService.changeUsername();
					break;
				}

				case 0:
					break loop;

				default:
					break;
			}
		}
	}
}
