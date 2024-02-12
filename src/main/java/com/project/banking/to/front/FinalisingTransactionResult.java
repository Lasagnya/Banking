package com.project.banking.to.front;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FinalisingTransactionResult {
	private OngoingTransaction ongoingTransaction;
	private ApiError apiError;
}
