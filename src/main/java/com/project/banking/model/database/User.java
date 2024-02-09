package com.project.banking.model.database;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 *  Класс пользователя
 */

@Getter
@Setter
@Entity
@Table(name = "my_user")
public class User {
	/** id пользователя */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id")
	private int id;

	/** имя пользователя */
	@Column(name = "user_name")
	private String name;

	/** пароль пользователя */
	@Column(name = "user_password")
	private String password;

	/** id банка-владельца */
	@ManyToOne
	@JoinColumn(name = "user_bank_id")
	private Bank bank;

	@OneToMany(mappedBy = "user")
	private List<Account> accounts;
}
