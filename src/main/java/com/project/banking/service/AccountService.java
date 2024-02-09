package com.project.banking.service;

import com.project.banking.enumeration.Period;
import com.project.banking.model.database.Account;
import com.project.banking.model.database.TransactionDb;

import java.util.List;
import java.util.Optional;

public interface AccountService {
	/**
	 * Пополнение счёта
	 * @param transaction исполняемая транзация
	 */
	void payIn(TransactionDb transaction);

	/**
	 * Снятие средств со счёта
	 * @param transaction исполняемая транзакия
	 */
	void withdrawal(TransactionDb transaction);

	/**
	 * Перевод средств между счетами
	 * @param transaction исполняемая транзакия
	 */
	void transfer(TransactionDb transaction);

	/**
	 * Получение сущности счёт
	 * @param id id счёта
	 * @return возвращает счёт, если он найден по id, иначе empty
	 */
	Optional<Account> findById(int id);

	/**
	 * Поиск всех счетов, принадлежащих банку с id
	 * @param id id банка-владельца
	 * @return возвращает список найденых счетов
	 */
	List<Account> findByBank(int id);

	/**
	 * Проверяет, приндлежит ли счёт с id accountId банку с id bankId
	 * @param bankId id банка для выяснения принадлежности
	 * @param accountId id счёта для выяснения принадлежности
	 * @return true, если принадлежит; false, если не принадлежит
	 */
	boolean thisBank(int bankId, int accountId);

	/**
	 * Поиск счетов по их принадлежности пользователю с id
	 * @param id id пользователя
	 * @return список найденных счетов
	 */
	List<Account> findByUser(int id);

	/**
	 * Сохранение нового счёта в базу данных
	 * @param account новый счёт
	 */
	void save(Account account);

	/**
	 * Создание выписки по счёту в txt файл
	 * @param account по этому счёту осуществляется выписка
	 * @param period выписка по этому периоду
	 */
	void excerpt(Account account, Period period);

	/**
	 * Создание выписки по счёту в pdf файл
	 * @param account по этому счёту осуществляется выписка
	 * @param period выписка по этому периоду
	 */
	void excerptInPDF(Account account, Period period);

	void chargePercents();
}
