package com.project.banking.to.client;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.banking.enumeration.Currency;
import com.project.banking.enumeration.TransactionStatus;
import com.project.banking.domain.ClientInformation;
import com.project.banking.domain.Transaction;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class Callback {
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

	public Callback(Transaction transaction, TransactionIncoming transactionIncoming) {
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

	public Callback(Transaction transaction, ClientInformation clientInformation) {
		id = transaction.getId();
		time = transaction.getTime();
		invoiceId = clientInformation.getInvoiceId();
		status = transaction.getStatus();
		sendingBank = transaction.getSendingBank();
		receivingBank = transaction.getReceivingBank();
		sendingAccount = transaction.getSendingAccount();
		receivingAccount = transaction.getReceivingAccount();
		amount = transaction.getAmount();
		currency = transaction.getCurrency();
		callbackUri = clientInformation.getCallbackUri();
	}

	public static Callback generateInvalidCallback(TransactionIncoming transactionIncoming) {
		Callback callback = new Callback();
		callback.id = 0;
		callback.time = new Date();
		callback.invoiceId = transactionIncoming.getInvoiceId();
		callback.status = TransactionStatus.INVALID;
		callback.sendingBank = 0;
		callback.receivingBank = transactionIncoming.getReceivingBank();
		callback.sendingAccount = transactionIncoming.getSendingAccount();
		callback.receivingAccount = transactionIncoming.getReceivingAccount();
		callback.amount = transactionIncoming.getAmount();
		callback.currency = transactionIncoming.getCurrency();
		callback.callbackUri = transactionIncoming.getCallbackUri();
		return callback;
	}
}
