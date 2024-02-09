package com.project.banking.model.database;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.banking.model.TransactionCallback;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@Entity
@Table(name = "transaction_callback")
public class TransactionCallbackDb {
	/** id внутри банкинга */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int id;

	/** id клиента */
	@Column(name = "invoice_id")
	private int invoiceId;

	@JsonIgnore
	@Column(name = "callback_uri")
	private String callbackUri;

	public TransactionCallbackDb(TransactionCallback transaction) {
		id = transaction.getId();
		invoiceId = transaction.getInvoiceId();
		callbackUri = transaction.getCallbackUri();
	}
}
