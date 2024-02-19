package com.project.banking.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.banking.enumeration.Currency;
import com.project.banking.enumeration.TransactionStatus;
import com.project.banking.enumeration.TypeOfTransaction;
import com.project.banking.to.client.TransactionIncoming;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Cascade;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Entity
@Table(name = "transaction")
public class Transaction {
	/** поле id */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int id;

	/** поле время совершения транзакции */
	@Column(name = "execution_time")
	@Temporal(TemporalType.TIMESTAMP)
	private Date time;

	/** поле тип транзакции */
	@Column(name = "type_of_transaction")
	@Enumerated(EnumType.STRING)
	private TypeOfTransaction typeOfTransaction;

	/** поле банк-отправитель */
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

	/** поле валюта транзакции */
	@Column(name = "transaction_currency")
	@Enumerated(EnumType.STRING)
	private Currency currency;

	@Column(name = "transaction_status")
	@Enumerated(EnumType.STRING)
	private TransactionStatus status;

	@Column(name = "confirmation_code")
	private Integer confirmationCode = null;

	@OneToOne(mappedBy = "transaction", fetch = FetchType.EAGER)
	@Cascade(org.hibernate.annotations.CascadeType.PERSIST)
	@JsonIgnore
	private ClientInformation clientInformation;

	/**
	 *
	 * @param time время выполнения
	 * @param typeOfTransaction тип транзакции
	 * @param sendingBank банк-отправитель
	 * @param receivingBank банк-получатель
	 * @param sendingAccount аккаунт-отправитель
	 * @param receivingAccount аккаунт-получатель
	 * @param amount сумма транзакции
	 * @param currency валюта транзакции
	 */
	public Transaction(Date time, TypeOfTransaction typeOfTransaction, int sendingBank, int receivingBank, int sendingAccount, int receivingAccount, double amount, Currency currency) {
		this.time = time;
		this.typeOfTransaction = typeOfTransaction;
		this.sendingBank = sendingBank;
		this.receivingBank = receivingBank;
		this.sendingAccount = sendingAccount;
		this.receivingAccount = receivingAccount;
		this.amount = amount;
		this.currency = currency;
	}

	public Transaction(TransactionIncoming transactionIncoming) {
		time = new Date();
		typeOfTransaction = TypeOfTransaction.TRANSFER;
		receivingBank = transactionIncoming.getReceivingBank();
		receivingAccount = transactionIncoming.getReceivingAccount();
		sendingBank = 1;									//по идее, банк устанавливает банкинг по номеру счёта
		sendingAccount = transactionIncoming.getSendingAccount();
		amount = transactionIncoming.getAmount();
		currency = transactionIncoming.getCurrency();
		status = TransactionStatus.PENDING;
		this.clientInformation = new ClientInformation(0, transactionIncoming.getInvoiceId(), transactionIncoming.getCallbackUri(), this);
	}

	public Transaction(int id) {
		this.id = id;
	}

	public Transaction(int id, Date time, TypeOfTransaction typeOfTransaction, int sendingBank, int receivingBank, int sendingAccount, int receivingAccount, double amount, Currency currency, TransactionStatus status, Integer confirmationCode) {
		this.id = id;
		this.time = time;
		this.typeOfTransaction = typeOfTransaction;
		this.sendingBank = sendingBank;
		this.receivingBank = receivingBank;
		this.sendingAccount = sendingAccount;
		this.receivingAccount = receivingAccount;
		this.amount = amount;
		this.currency = currency;
		this.status = status;
		this.confirmationCode = confirmationCode;
	}

	@Override
	public String toString() {
		return "Transaction{" +
				"id=" + id +
				", time=" + time +
				", typeOfTransaction=" + typeOfTransaction +
				", sendingBank=" + sendingBank +
				", receivingBank=" + receivingBank +
				", sendingAccount=" + sendingAccount +
				", receivingAccount=" + receivingAccount +
				", amount=" + amount +
				", currency=" + currency +
				", status=" + status +
				", confirmationCode=" + confirmationCode +
				", clientInformation=" + clientInformation +
				'}';
	}
}