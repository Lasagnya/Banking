package com.project.banking.model.database;

import com.project.banking.enumeration.Currency;
import com.project.banking.enumeration.TransactionStatus;
import com.project.banking.enumeration.TypeOfTransaction;
import com.project.banking.model.TransactionIncoming;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class TransactionDb {
	/** поле id */
	private int id;

	/** поле время совершения транзации */
	private Date time;

	/** поле тип транзакии */
	private TypeOfTransaction typeOfTransaction;

	/** поле бонк-отправитель */
	private int sendingBank;

	/** поле банк-получатель */
	private int receivingBank;

	/** поле аккаунт-отправитель */
	private int sendingAccount;

	/** поле аккаунт-получатель */
	private int receivingAccount;

	/** поле сумма транзакции */
	private double amount;

	/** поле валюта транзации */
	private Currency currency;

	private TransactionStatus status;

	private Integer confirmationCode = null;

	/**
	 *
	 * @param time время выполнения
	 * @param typeOfTransaction тип транзации
	 * @param sendingBank банк-отправитель
	 * @param receivingBank банк-получатель
	 * @param sendingAccount аккаунт-отправитель
	 * @param receivingAccount аккаунт-получатель
	 * @param amount сумма транзации
	 * @param currency валюта транзакции
	 */
	public TransactionDb(Date time, TypeOfTransaction typeOfTransaction, int sendingBank, int receivingBank, int sendingAccount, int receivingAccount, double amount, Currency currency) {
		this.time = time;
		this.typeOfTransaction = typeOfTransaction;
		this.sendingBank = sendingBank;
		this.receivingBank = receivingBank;
		this.sendingAccount = sendingAccount;
		this.receivingAccount = receivingAccount;
		this.amount = amount;
		this.currency = currency;
	}

	public TransactionDb(TransactionIncoming transactionIncoming) {
		time = new Date();
		typeOfTransaction = TypeOfTransaction.TRANSFER;
		receivingBank = transactionIncoming.getReceivingBank();
		receivingAccount = transactionIncoming.getReceivingAccount();
		sendingBank = 1;									//по идее, банк устанавливает банкинг по номеру счёта
		sendingAccount = transactionIncoming.getSendingAccount();
		amount = transactionIncoming.getAmount();
		currency = transactionIncoming.getCurrency();
		status = TransactionStatus.PENDING;
	}
}