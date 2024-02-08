package com.project.banking.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.project.banking.enumeration.Currency;
import com.project.banking.enumeration.TransactionStatus;
import jakarta.persistence.Transient;
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
	@Transient
	@JsonInclude
	private TransactionStatus status;

	@Transient
	private int sendingBank;

	@Transient
	private int receivingBank;

	@Transient
	private int sendingAccount;

	@Transient
	private int receivingAccount;

	@Transient
	private double amount;

	@Transient
	private Currency currency;

	@JsonIgnore
	private String callbackUri;

	public TransactionCallback(Transaction transaction, TransactionIncoming transactionIncoming) {
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
