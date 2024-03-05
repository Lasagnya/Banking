package com.project.banking.to.client;

import com.project.banking.enumeration.Currency;
import lombok.*;

/**
 *  Класс транзакций
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class TransactionIncoming {

	private int invoiceId;

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

	private String callbackUri;
}
