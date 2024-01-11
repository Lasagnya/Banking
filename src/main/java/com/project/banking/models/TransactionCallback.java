package com.project.banking.models;

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
	private int invoice_id;

	/** статус транзакции банкинга */
	private TransactionStatus status;

	private int sendingBank;

	private int receivingBank;

	private int sendingAccount;

	private int receivingAccount;

	private double amount;

	private Currency currency;
}
