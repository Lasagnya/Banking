package com.project.banking.model.database;

import com.project.banking.enumeration.Currency;
import com.project.banking.enumeration.TransactionStatus;
import com.project.banking.enumeration.TypeOfTransaction;
import com.project.banking.model.TransactionIncoming;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Entity
@Table(name = "transaction")
public class TransactionDb {
	/** поле id */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "transaction_id")
	private int id;

	/** поле время совершения транзации */
	@Column(name = "execution_time")
	@Temporal(TemporalType.TIMESTAMP)
	private Date time;

	/** поле тип транзакии */
	@Column(name = "type_of_transaction")
	@Enumerated(EnumType.STRING)
	private TypeOfTransaction typeOfTransaction;

	/** поле бонк-отправитель */
	@Column(name = "sending_bank")
	private int sendingBank;

	/** поле банк-получатель */
	@Column(name = "receiving_bank")
	private int receivingBank;

	/** поле аккаунт-отправитель */
	@Column(name = "sending_account")
	private int sendingAccount;

	/** поле аккаунт-получатель */
	@Column(name = "receiving_account")
	private int receivingAccount;

	/** поле сумма транзакции */
	@Column(name = "amount")
	private double amount;

	/** поле валюта транзации */
	@Column(name = "transaction_currency")
	@Enumerated(EnumType.STRING)
	private Currency currency;

	@Column(name = "transaction_status")
	@Enumerated(EnumType.STRING)
	private TransactionStatus status;

	@Column(name = "confirmation_code")
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