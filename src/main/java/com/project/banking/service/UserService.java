package com.project.banking.service;

import com.project.banking.domain.User;
import com.project.banking.to.front.AuthenticationDTO;

import java.util.Optional;

public interface UserService {
	/**
	 * Поиск пользователя в базе данных по имени
	 * @param name имя, по которому ищется пользователь
	 * @return найденный пользователь или empty, если не найден
	 */
	Optional<User> findByName(String name);

	/**
	 * Сохранение нового пользователя в базу данных
	 *
	 * @param user пользователь для сохранения
	 * @return
	 */
	User save(User user);

	/**
	 * В методе происходит аутентификация пользователя. Человек вводит имя пользователя,
	 * и, если такого не существует, ему предлагается создать нового с таким именем или ввести ещё раз.
	 * Далее происходит ввод пароля.
	 */
	void authentication();

	User getUser();

	/**
	 * Обновление записи о пользователе в базе данных.
	 *
	 * @param updatedUser обновлённый пользователь
	 */
	void update(User updatedUser);

	/**
	 * Изменяет пароль у локального пользователя и обновляет базу данных
	 */
	void changePassword();

	/**
	 * Обновляет имя у локального пользователя и обновляет базу данных
	 */
	void changeUsername();

	AuthenticationDTO authenticatedUser();

	AuthenticationDTO authenticatedAdmin();
}
