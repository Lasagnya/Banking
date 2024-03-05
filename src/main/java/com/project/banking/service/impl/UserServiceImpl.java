package com.project.banking.service.impl;

import com.project.banking.domain.User;
import com.project.banking.repository.UserRepository;
import com.project.banking.service.UserService;
import com.project.banking.to.front.AuthenticationDTO;
import jakarta.persistence.Transient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Scanner;

@Service
public class UserServiceImpl implements UserService {
	private final UserRepository userRepository;
	private static User user;
	private final PasswordEncoder passwordEncoder;
	private final AuthenticationManager authenticationManager;
	private final UserDetailsService userDetailsService;

	@Autowired
	public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, UserDetailsService userDetailsService) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.authenticationManager = authenticationManager;
		this.userDetailsService = userDetailsService;
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
	public User save(User user) {
		return userRepository.save(user);
	}

	private String getPasswordHash() {
		Scanner scanner = new Scanner(System.in);
		String password;
		String password2;
		do {
			System.out.println("Введите пароль:");
			password = scanner.next();
			System.out.println("Введите пароль ещё раз:");
			password2 = scanner.next();
			if (password2.equals(password)) {
				break;
			} else System.out.println("Пароли не совпадают, попробуйте ещё раз!");
		} while (true);
		return passwordEncoder.encode(password);
	}

	@Override
	public void authentication() {
		Scanner scanner = new Scanner(System.in);
		System.out.println("Необходимо войти в аккаунт.\n" +
				"Введите имя пользователя:");
		String name = scanner.next();
		Optional<User> authUser = findByName(name);


		while (authUser.isEmpty()) {
			System.out.println("""
					Такого пользователя не существует. Хотите создать?
					1: создать пользователя
					2: ввести имя ещё раз""");

			if (scanner.nextInt() == 1) {
				User newUser = new User();
				newUser.setName(name);
				newUser.setEncodedPassword(getPasswordHash().getBytes());
				save(newUser);
			}

			System.out.println("Необходимо войти в аккаунт\n" +
					"Введите имя пользователя:");
			authUser = findByName(scanner.next());
		}

		System.out.println("Введите пароль:");
		String password = scanner.next();
		while (!passwordEncoder.matches(password, new String(authUser.get().getEncodedPassword()))) {
			System.out.println("Неверный пароль, попробуйте ещё раз!");
			password = scanner.next();
		}
		defineUser(authUser.get());
//		authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(name, password));
	}

	@Override
	public void update(User updatedUser) {
		userRepository.save(updatedUser);
	}

	@Override
	@Transient
	public void changePassword() {
//		UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//		Optional<User> user1 = findByName(userDetails.getUsername());
//		user1.ifPresent(o -> o.setBytePasswordHash(getEncodedPassword().getBytes()));
		user.setEncodedPassword(getPasswordHash().getBytes());
		update(user);
		System.out.println("Пароль изменён!");
	}

	@Override
	@Transient
	public void changeUsername() {
		Scanner scanner = new Scanner(System.in);
		System.out.println("Введите новое имя пользователя:");
		String name = scanner.next();
//		UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//		Optional<User> user1 = findByName(userDetails.getUsername());
//		user1.ifPresent(o -> o.setName(name));
		user.setName(name);
		update(user);
		System.out.println("Имя изменено на " + name + ".");
	}

	@PreAuthorize("hasRole('USER')")
	public AuthenticationDTO authenticatedUser() {
		UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		return new AuthenticationDTO(userDetails.getUsername(), userDetails.getPassword());
	}

	@PreAuthorize("hasRole('ADMIN')")
	public AuthenticationDTO authenticatedAdmin() {
		UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		return new AuthenticationDTO(userDetails.getUsername(), userDetails.getPassword());
	}
}
