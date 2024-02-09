package com.project.banking.model.database;

import com.project.banking.enumeration.Currency;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

/**
 *  Класс счёта
 */


@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "account")
public class Account {
	/** поле id */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "account_id")
	private int id;

	/** поле валюта */
	@Column(name = "currency")
	@Enumerated(EnumType.STRING)
	private Currency currency;

	/** поле дата открытия */
	@Column(name = "opening")
	private Date opening;

	/** поле баланс */
	@Column(name = "balance")
	private double balance = 0.0;

	/** поле id банка */
	@ManyToOne
	@JoinColumn(name = "account_bank_id")
	private Bank bank;

	/** поле id пользователя */
	@ManyToOne
	@JoinColumn(name = "account_user_id")
	private User user;

	/** поле нужно ли начислять проценты */
	@Column(name = "account_is_percents")
	private boolean isPercents = false;

	/**
	 *
	 * @param currency валюта счёта
	 * @param opening дата открытия
	 * @param bank банк-владелец
	 * @param user пользователь-владелец
	 */
	public Account(Currency currency, Date opening, Bank bank, User user) {
		this.currency = currency;
		this.opening = opening;
		this.bank = bank;
		this.user = user;
	}
}
