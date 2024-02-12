package com.project.banking.service.impl;

import com.project.banking.domain.User;
import com.project.banking.repository.UserRepository;
import com.project.banking.service.UserService;
import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Optional;
import java.util.Scanner;

@Service
public class UserServiceImpl implements UserService {
	private final UserRepository userRepository;
	private static User user;

	@Autowired
	public UserServiceImpl(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	private void defineUser(User user) {
		UserServiceImpl.user = user;
	}

	@Override
	public User getUser() {
		return user;
	}

	@Override
	public Optional<User> findByName(String name) {
		return userRepository.findByName(name);
	}

	@Override
	public void save(User user) {
		userRepository.save(user);
	}

	private String getPasswordHash() {
		Scanner scanner = new Scanner(System.in);
		byte[] password;
		byte[] password2;
		do {
			System.out.println("Введите пароль:");
			password = scanner.next().getBytes(StandardCharsets.UTF_8);
			System.out.println("Введите пароль ещё раз:");
			password2 = scanner.next().getBytes(StandardCharsets.UTF_8);
			if (Arrays.equals(password2, password)) {
				break;
			} else System.out.println("Пароли не совпадают, попробуйте ещё раз!");
		} while (true);
		Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id, 16, 32);
		String hash = argon2.hash(22, 65536, 1, password);
		argon2.wipeArray(password);
		argon2.wipeArray(password2);
		return hash;
	}

	@Override
	public void authentication() {
		Scanner scanner = new Scanner(System.in);
		User user;
		System.out.println("Необходимо войти в аккаунт.\n" +
				"Введите имя пользователя:");
		String name = scanner.next();

		while (findByName(name).isEmpty()) {
			System.out.println("""
					Такого пользователя не существует. Хотите создать?
					1: создать пользователя
					2: ввести имя ещё раз""");

			if (scanner.nextInt() == 1) {
				user = new User();
				user.setName(name);
				user.setPassword(getPasswordHash());
				save(user);
			}

			System.out.println("Необходимо войти в аккаунт\n" +
					"Введите имя пользователя:");
			name = scanner.next();
		}
		user = findByName(name).get();

		System.out.println("Введите пароль:");
		byte[] password = scanner.next().getBytes(StandardCharsets.UTF_8);
		Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id, 16, 32);
		while (!argon2.verify(user.getPassword(), password)) {
			System.out.println("Неверный пароль, попробуйте ещё раз!");
			password = scanner.next().getBytes(StandardCharsets.UTF_8);
		}
		argon2.wipeArray(password);
		defineUser(user);
	}

	@Override
	public void update(User updatedUser) {
		userRepository.save(updatedUser);
	}

	@Override
	public void changePassword() {
		user.setPassword(getPasswordHash());
		update(user);
		System.out.println("Пароль изменён!");
	}

	@Override
	public void changeUsername() {
		Scanner scanner = new Scanner(System.in);
		System.out.println("Введите новое имя пользователя:");
		String name = scanner.next();
		user.setName(name);
		update(user);
		System.out.println("Имя изменено на " + name + ".");
	}
}
