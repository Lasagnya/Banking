package com.project.banking.functions;

import com.project.banking.dao.AccountDAO;
import com.project.banking.dao.BankDAO;
import com.project.banking.dao.UserDAO;
import com.project.banking.models.Account;
import com.project.banking.models.Bank;
import com.project.banking.models.Period;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StreamTokenizer;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * Класс с функциями для ввода значений из интерфейса
 */

public class SwitchInputMethods {
	private static final AccountDAO accountDAO = new AccountDAO();
	private static final BankDAO bankDAO = new BankDAO();
	private final Scanner scanner = new Scanner(System.in);

	/**
	 * Получение банка-получателя
	 * @return введённый id банка
	 */
	public int getReceivingBank() {
		List<Bank> banks = bankDAO.findAll();
		for (Bank bank : banks)
			System.out.println(bank.getId() + ": " + bank.getName());
		return scanner.nextInt();
	}

	/**
	 * Получение аккаунта-получателя с проверкой его принадлежности выбранному банку
	 * @param receivingBank банк-получатель
	 * @return введённый id счёта
	 */
	public int getReceivingAccount(int receivingBank) {
		int receivingAccount = scanner.nextInt();
		while (!accountDAO.thisBank(receivingBank, receivingAccount)) {
			System.out.println("Счёт не найден, попробуйте ещё раз.");
			receivingAccount = scanner.nextInt();
		}
		return receivingAccount;
	}

	/**
	 * Получение аккаунта-отправителя
	 * @return введённый id аккаунта
	 */
	public Account getSendingAccount() {
		StreamTokenizer st = new StreamTokenizer(new BufferedReader(new InputStreamReader(System.in)));
		List<Account> accounts = accountDAO.findByUser(UserDAO.getUser().getId());
		for (Account account : accounts)
			System.out.println("Номер счёта: " + account.getId() +
					", " + account.getBalance() + " " + account.getCurrency().toString());
		try {
			st.nextToken();
			Optional<Account> account = accounts.stream().filter(e -> e.getId() == (int) st.nval).findAny();
			while (account.isEmpty()) {
				System.out.println("Счёт с таким номером не найден, попробуйте ещё раз.");
				st.nextToken();
				account = accounts.stream().filter(e -> e.getId() == (int) st.nval).findAny();
			}
			return account.get();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Получение суммы транзакции с проверкой на доступность средств на счёте-отправителе
	 * @param sendingAccount счёт-отправитель
	 * @return сумма транзакции
	 */
	public double getAmount(Account sendingAccount) {
		double amount = scanner.nextDouble();
		double balance = sendingAccount.getBalance();
		while (amount > balance) {
			System.out.println("Недостаточно средств! Введите другую сумму.");
			amount = scanner.nextDouble();
		}
		return amount;
	}

	/**
	 * Получение периода выписки
	 * @return период выписки
	 */
	public Period getPeriod() {
		int value = scanner.nextInt();
		while(true) {
			switch (value) {
				case 1:
					return Period.MONTH;
				case 2:
					return Period.YEAR;
				case 3:
					return Period.ALL;
				default:
					System.out.println("Введено неверное значение, попробуйте ещё раз.");
					value = scanner.nextInt();
			}
		}
	}

	/**
	 * Получение формата файла для выписки. Но, в целом, можно использовать для любого бинарного ввода
	 * @return 1 или 2 в зависимости от того, что пользователь ввёл
	 */
	public int getFileFormat() {
		int file = scanner.nextInt();
		while ((file != 1) && (file != 2)) {
			System.out.println("Введено неверное значение, попробуйте ещё раз.");
			file = scanner.nextInt();
		}
		return file;
	}
}
