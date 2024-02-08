package com.project.banking.dao;

import com.project.banking.enumeration.Currency;
import com.project.banking.enumeration.TransactionStatus;
import com.project.banking.model.*;
import org.springframework.stereotype.Component;

import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.Optional;
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
					"insert into transaction_callback(id, invoice_id, callback_uri) " +
							"values(?, ?, ?)");
			preparedStatement1.setInt(1, transaction.getId());
			preparedStatement1.setInt(2, transaction.getInvoiceId());
			preparedStatement1.setString(3, transaction.getCallbackUri());
			preparedStatement1.executeUpdate();
		} catch (SQLException e) {
			if (!e.getSQLState().equals("23505"))
				throw new RuntimeException(e);
		}
	}

//	public TransactionCallback fillAndSave(Transaction transaction, TransactionIncoming transactionIncoming) {
//		TransactionCallback transactionCallback = new TransactionCallback(transaction, transactionIncoming);
//		saveTransaction(transactionCallback);
//		return transactionCallback;
//	}

	public Optional<TransactionCallback> findById(int id) {
		TransactionCallback transaction = new TransactionCallback();
		try {
			PreparedStatement preparedStatement = connection.prepareStatement("select id, invoice_id, callback_uri, execution_time, sending_bank, receiving_bank, sending_account, receiving_account, amount, transaction_currency, transaction_status from " +
					"transaction_callback join public.transaction on transaction_callback.id = transaction.transaction_id " +
					"where id=?");
			preparedStatement.setInt(1, id);
			ResultSet rs = preparedStatement.executeQuery();
			if (rs.next()) {
				transaction.setId(rs.getInt("id"));
				transaction.setInvoiceId(rs.getInt("invoice_id"));
				transaction.setCallbackUri(rs.getString("callback_uri"));
				transaction.setTime(rs.getTimestamp("execution_time"));
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
}
