package com.project.banking.model.database;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.banking.model.TransactionCallback;
import com.project.banking.model.TransactionIncoming;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class TransactionCallbackDb {
	/** id внутри банкинга */
	private int id;

	/** id клиента */
	private int invoiceId;

	@JsonIgnore
	private String callbackUri;

	public TransactionCallbackDb(TransactionCallback transaction) {
		id = transaction.getId();
		invoiceId = transaction.getInvoiceId();
		callbackUri = transaction.getCallbackUri();
	}
}
