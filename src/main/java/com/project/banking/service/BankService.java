package com.project.banking.service;

import com.project.banking.model.database.Bank;

import java.util.List;
import java.util.Optional;

public interface BankService {
	/**
	 * Поиск всех банков в базе данных
	 * @return все найденные банки
	 */
	List<Bank> findAll();

	Optional<Bank> findById(int id);

	/**
	 * Удаление банка с id из базы данных
	 * @param id id банка
	 */
	void delete(int id);

	/**
	 * Изменение имени банка по id
	 * @param updatedBank изменённый банк
	 */
	void update(Bank updatedBank);

	/**
	 * Сохранение нового банка в базу данных
	 * @param bank банк для сохранения
	 */
	void save(Bank bank);
}
