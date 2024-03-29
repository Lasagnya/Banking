package com.project.banking.dao;

import com.project.banking.models.Bank;

import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

/**
 *  Класс методов для сущности Bank. Взаимодействует
 *  с таблицей bank.
 */

public class BankDAO {
	private static final String DRIVER;
	private static final String URL;
	private static final String USERNAME;
	private static final String PASSWORD;
	private static final Connection connection;
	private static final Properties properties;

	static {
		try {
			properties = new Properties();
			FileReader reader = new FileReader("src/main/resources/application.properties");
			properties.load(reader);
			DRIVER = properties.getProperty("spring.datasource.driver-class-name");
			URL = properties.getProperty("spring.datasource.url");
			USERNAME = properties.getProperty("spring.datasource.username");
			PASSWORD = properties.getProperty("spring.datasource.password");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		try {
			Class.forName(DRIVER);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}

		try {
			connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Поиск всех банков в базе данных
	 * @return все найденные банки
	 */
	public List<Bank> findAll() {
		List<Bank> banks = new ArrayList<>();
		try {
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery("select * from bank");

			while (resultSet.next()) {
				Bank bank = new Bank();
				bank.setId(resultSet.getInt("bank_id"));
				bank.setName(resultSet.getString("bank_name"));
				banks.add(bank);
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return banks;
	}

	public Optional<Bank> findById(int id) {
		Bank bank = new Bank();
		try {
			PreparedStatement preparedStatement = connection.prepareStatement("select * from bank where bank_id=?");
			preparedStatement.setInt(1, id);
			ResultSet rs = preparedStatement.executeQuery();
			if (rs.next()) {
				bank.setId(rs.getInt("bank_id"));
				bank.setName(rs.getString("bank_name"));
				return Optional.of(bank);
			}
			else return Optional.empty();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Удаление банка с id из базы данных
	 * @param id id банка
	 */
	public void delete(int id) {
		try {
			PreparedStatement preparedStatement = connection.prepareStatement("delete from bank where bank_id=?");
			preparedStatement.setInt(1, id);
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Изменение имени банка по id
	 * @param updatedBank изменённый банк
	 */
	public void update(Bank updatedBank) {
		try {
			PreparedStatement preparedStatement = connection.prepareStatement("update bank set bank_name=? where bank_id=?");
			preparedStatement.setString(1, updatedBank.getName());
			preparedStatement.setInt(2, updatedBank.getId());
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Сохранение нового банка в базу данных
	 * @param bank банк для сохранения
	 */
	public void save(Bank bank) {
		try {
			PreparedStatement preparedStatement = connection.prepareStatement("insert into bank(bank_name) values(?)");
			preparedStatement.setString(1, bank.getName());
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
}
