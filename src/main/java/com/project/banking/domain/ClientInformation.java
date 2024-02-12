package com.project.banking.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.banking.to.client.Callback;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Entity
@Table(name = "client_information")
public class ClientInformation {
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

	@OneToOne
	@JoinColumn(name = "transaction_id", referencedColumnName = "id")
	@JsonIgnore
	private Transaction transaction;

	public ClientInformation(Callback transaction) {
		id = transaction.getId();
		invoiceId = transaction.getInvoiceId();
		callbackUri = transaction.getCallbackUri();
	}

	public ClientInformation(int id, int invoiceId, String callbackUri) {
		this.id = id;
		this.invoiceId = invoiceId;
		this.callbackUri = callbackUri;
	}
}
