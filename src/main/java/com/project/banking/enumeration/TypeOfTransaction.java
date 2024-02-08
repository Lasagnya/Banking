package com.project.banking.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 *  Типы транзации
 */

@AllArgsConstructor
@Getter
public enum TypeOfTransaction {
	TRANSFER ("Перевод"), WITHDRAWAL ("Снятие"), PAYIN ("Пополнение");

	/** Название для вывода */
	private final String title;
}
