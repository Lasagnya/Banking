package com.project.banking.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.banking.enumeration.Currency;
import com.project.banking.enumeration.TransactionStatus;
import com.project.banking.model.database.TransactionCallbackDb;
import com.project.banking.model.database.TransactionDb;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class TransactionCallback {
	/** id внутри банкинга */
	private int id;

	/** поле время совершения транзации */
	private Date time;

	/** id клиента */
	private int invoiceId;

	/** статус транзакции банкинга */
	private TransactionStatus status;

	private int sendingBank;

	private int receivingBank;

	private int sendingAccount;

	private int receivingAccount;

	private double amount;

	private Currency currency;

	@JsonIgnore
	private String callbackUri;

	public TransactionCallback(TransactionDb transaction, TransactionIncoming transactionIncoming) {
		id = transaction.getId();
		time = transaction.getTime();
		invoiceId = transactionIncoming.getInvoiceId();
		status = transaction.getStatus();
		sendingBank = transaction.getSendingBank();
		receivingBank = transaction.getReceivingBank();
		sendingAccount = transaction.getSendingAccount();
		receivingAccount = transaction.getReceivingAccount();
		amount = transaction.getAmount();
		currency = transaction.getCurrency();
		callbackUri = transactionIncoming.getCallbackUri();
	}

	public TransactionCallback(TransactionCallbackDb transactionCallbackDb) {

	}

	public static TransactionCallback generateInvalidCallback(TransactionIncoming transactionIncoming) {
		TransactionCallback transactionCallback = new TransactionCallback();
		transactionCallback.id = 0;
		transactionCallback.time = new Date();
		transactionCallback.invoiceId = transactionIncoming.getInvoiceId();
		transactionCallback.status = TransactionStatus.INVALID;
		transactionCallback.sendingBank = 0;
		transactionCallback.receivingBank = transactionIncoming.getReceivingBank();
		transactionCallback.sendingAccount = transactionIncoming.getSendingAccount();
		transactionCallback.receivingAccount = transactionIncoming.getReceivingAccount();
		transactionCallback.amount = transactionIncoming.getAmount();
		transactionCallback.currency = transactionIncoming.getCurrency();
		transactionCallback.callbackUri = transactionIncoming.getCallbackUri();
		return transactionCallback;
	}
}
