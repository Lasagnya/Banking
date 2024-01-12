package com.project.banking.dao;

import com.project.banking.models.*;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Optional;
import java.util.Properties;

@Component
public class TransactionDAO {
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

	public Optional<Transaction> findById(int id) {
		Transaction transaction = new Transaction();
		try {
			PreparedStatement preparedStatement = connection.prepareStatement("select * from transaction where transaction_id=?");
			preparedStatement.setInt(1, id);
			ResultSet rs = preparedStatement.executeQuery();
			if (rs.next()) {
				transaction.setId(rs.getInt("transaction_id"));
				transaction.setTime(rs.getTimestamp("execution_time"));
				transaction.setTypeOfTransaction(TypeOfTransaction.valueOf(rs.getString("type_of_transaction")));
				transaction.setSendingBank(rs.getInt("sending_bank"));
				transaction.setReceivingBank(rs.getInt("receiving_bank"));
				transaction.setSendingAccount(rs.getInt("sending_account"));
				transaction.setReceivingAccount(rs.getInt("receiving_account"));
				transaction.setAmount(rs.getDouble("amount"));
				transaction.setCurrency(Currency.valueOf(rs.getString("transaction_currency")));
				transaction.setStatus(TransactionStatus.valueOf(rs.getString("transaction_status")));
				return Optional.of(transaction);
			}
			else return Optional.empty();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Сохранение транзакции в базу данных
	 * @param transaction транзакция для сохранения
	 */
	public Transaction saveTransaction(Transaction transaction) {
		try {
			PreparedStatement preparedStatement1 = connection.prepareStatement(
					"insert into transaction(execution_time, type_of_transaction, sending_bank, receiving_bank, sending_account, receiving_account, amount, transaction_currency, transaction_status) values(?, ?, ?, ?, ?, ?, ?, ?, ?)",
					Statement.RETURN_GENERATED_KEYS);
			preparedStatement1.setTimestamp(1, new Timestamp(transaction.getTime().getTime()));
			preparedStatement1.setString(2, transaction.getTypeOfTransaction().toString());
			preparedStatement1.setInt(3, transaction.getSendingBank());
			preparedStatement1.setInt(4, transaction.getReceivingBank());
			preparedStatement1.setInt(5, transaction.getSendingAccount());
			preparedStatement1.setInt(6, transaction.getReceivingAccount());
			preparedStatement1.setDouble(7, transaction.getAmount());
			preparedStatement1.setString(8, transaction.getCurrency().toString());
			preparedStatement1.setString(9, transaction.getStatus().toString());
			preparedStatement1.executeUpdate();
			ResultSet generatedKeys = preparedStatement1.getGeneratedKeys();
			if (generatedKeys.next()) {
				transaction.setId(generatedKeys.getInt(1));
			}
			else {
				throw new SQLException("Creating transaction failed, no ID obtained.");
			}
			makeCheck(transaction);
		} catch (SQLException e) {
			if (!e.getSQLState().equals("23505"))
				throw new RuntimeException(e);
		}
		return transaction;
	}

	public Transaction fillAndSave(TransactionIncoming transactionIncoming) {
		Transaction transaction = new Transaction(transactionIncoming);
		return saveTransaction(transaction);
	}

	/**
	 * Создание чека по транзакции
	 * @param transaction транзакция, по которой создаётся чек
	 */
	private void makeCheck(Transaction transaction) {
		int id;
		String sendingBankName = "";
		String receivingBankName = "";
		int sendingAccountId = 0;
		int receivingAccountId = 0;
		try {
			PreparedStatement preparedStatement1 = connection.prepareStatement(
					"select * from transaction " +
							"join bank on sending_bank=bank.bank_id or receiving_bank=bank.bank_id " +
							"join account on sending_account=account.account_id or receiving_account=account.account_id " +
							"where " +
							"type_of_transaction=? and sending_bank=? and receiving_bank=? and sending_account=? and receiving_account=?" +
							" and amount=? and transaction_currency=? and execution_time=?"
			);
			preparedStatement1.setString(1, transaction.getTypeOfTransaction().toString());
			preparedStatement1.setInt(2, transaction.getSendingBank());
			preparedStatement1.setInt(3, transaction.getReceivingBank());
			preparedStatement1.setInt(4, transaction.getSendingAccount());
			preparedStatement1.setInt(5, transaction.getReceivingAccount());
			preparedStatement1.setDouble(6, transaction.getAmount());
			preparedStatement1.setString(7, transaction.getCurrency().toString());
			preparedStatement1.setTimestamp(8, new Timestamp(transaction.getTime().getTime()));
			ResultSet resultSet = preparedStatement1.executeQuery();
			resultSet.next();
			id = resultSet.getInt("transaction_id");
			sendingBankName = resultSet.getString("bank_name");
			sendingAccountId = resultSet.getInt("account_id");
			while (resultSet.next()) {
				receivingBankName = resultSet.getString("bank_name");
				receivingAccountId = resultSet.getInt("account_id");
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

		DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyy");
		DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
		try {
			Files.createDirectories(Path.of("check"));
			FileWriter fw = new FileWriter(String.format("check/check%d.txt", id));
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(String.format("%40s\n", "").replace(" ", "-"));
			String title = "Банковский чек";
			String output = String.format("|%13s%12s|\n", "#", "").replace("#", title);
			bw.write(output);
			bw.write(String.format("%-20s%21s", "| Чек:", String.format("%d |\n", id)));
			bw.write(String.format("%-20s%21s", "| " + dateFormat.format(transaction.getTime().getTime()), timeFormat.format(transaction.getTime().getTime()) + " |\n"));
			bw.write(String.format("%-20s%21s", "| Тип транзакции:", transaction.getTypeOfTransaction().getTitle() + " |\n"));
			bw.write(String.format("%-20s%21s", "| Банк отправителя:", String.format("%s |\n", sendingBankName)));
			if (transaction.getTypeOfTransaction() == TypeOfTransaction.TRANSFER)
				bw.write(String.format("%-20s%21s", "| Банк получателя:", String.format("%s |\n", receivingBankName)));
			bw.write(String.format("%-20s%21s", "| Счёт отправителя:", String.format("%s |\n", sendingAccountId)));
			if (transaction.getTypeOfTransaction() == TypeOfTransaction.TRANSFER)
				bw.write(String.format("%-20s%21s", "| Счёт отправителя:", String.format("%s |\n", receivingAccountId)));
			bw.write(String.format("%-20s%21s", "| Сумма:", String.format("%s |\n", transaction.getAmount() + " " + transaction.getCurrency().toString())));
			bw.write(String.format("%40s\n", "").replace(" ", "-"));
			bw.close();
			fw.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
