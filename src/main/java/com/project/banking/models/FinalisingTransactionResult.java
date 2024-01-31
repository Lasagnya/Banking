package com.project.banking.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FinalisingTransactionResult {
	private Transaction transaction;
	private ApiError apiError;
}
