package com.project.banking.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TransactionCallback {
	/** id внутри банкинга */
	private int id;

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

	public TransactionCallback(Transaction transaction, TransactionIncoming transactionIncoming) {
		id = transaction.getId();
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
