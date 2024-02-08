package com.project.banking.model.database;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 *  Класс банка
 */

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "bank")
public class Bank {
	/** поле id */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "bank_id")
	private int id;

	/** поле название */
	@Column(name = "bank_name")
	private String name;

	/** поле пользователи банка */
	@OneToMany(mappedBy = "bank")
	private List<User> users;

	@OneToMany(mappedBy = "bank")
	private List<Account> accounts;

	public Bank(int id) {
		this.id = id;
	}
}
