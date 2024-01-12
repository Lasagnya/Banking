package com.project.banking.dao;

import com.project.banking.models.Transaction;
import com.project.banking.models.TransactionCallback;
import com.project.banking.models.TransactionIncoming;
import org.springframework.stereotype.Component;

import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;

@Component
public class TransactionCallbackDAO {
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

	public void saveTransaction(TransactionCallback transaction) {
		try {
			PreparedStatement preparedStatement1 = connection.prepareStatement(
					"insert into transaction_callback(id, invoice_id, status, sending_bank, receiving_bank, sending_account, receiving_account, amount, transaction_currency, callback_uri) " +
							"values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
			preparedStatement1.setInt(1, transaction.getId());
			preparedStatement1.setInt(2, transaction.getInvoiceId());
			preparedStatement1.setString(3, transaction.getStatus().toString());
			preparedStatement1.setInt(4, transaction.getSendingBank());
			preparedStatement1.setInt(5, transaction.getReceivingBank());
			preparedStatement1.setInt(6, transaction.getSendingAccount());
			preparedStatement1.setInt(7, transaction.getReceivingAccount());
			preparedStatement1.setDouble(8, transaction.getAmount());
			preparedStatement1.setString(9, transaction.getCurrency().toString());
			preparedStatement1.setString(10, transaction.getCallbackUri());
			preparedStatement1.executeUpdate();
		} catch (SQLException e) {
			if (!e.getSQLState().equals("23505"))
				throw new RuntimeException(e);
		}
	}

	public void fillAndSave(Transaction transaction, TransactionIncoming transactionIncoming) {
		TransactionCallback transactionCallback = new TransactionCallback(transaction, transactionIncoming);
		saveTransaction(transactionCallback);
	}
}
