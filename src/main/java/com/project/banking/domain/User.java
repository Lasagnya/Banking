package com.project.banking.domain;

import com.project.banking.to.front.AuthenticationDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.modelmapper.ModelMapper;

import java.util.List;

/**
 *  Класс пользователя
 */

@Getter
@Setter
@NoArgsConstructor
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
	@Transient
	private String rawPassword;

	@Column(name = "encoded_password")
	private byte[] encodedPassword;

	@Column(name = "role")
	private String role = "ROLE_USER";

	/** id банка-владельца */
	@ManyToOne
	@JoinColumn(name = "user_bank_id")
	private Bank bank;

	@OneToMany(mappedBy = "user")
	private List<Account> accounts;

	public static User build(AuthenticationDTO auth) {
		ModelMapper modelMapper = new ModelMapper();
		return modelMapper.map(auth, User.class);
	}

	public User(AuthenticationDTO auth) {
		this.name = auth.getUsername();
		this.rawPassword = auth.getPassword();
		this.bank = new Bank(1);
	}
}
