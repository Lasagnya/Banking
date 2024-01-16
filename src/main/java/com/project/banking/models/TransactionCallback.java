package com.project.banking.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
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
}
