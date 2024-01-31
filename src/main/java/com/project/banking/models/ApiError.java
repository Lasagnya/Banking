package com.project.banking.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ApiError {
	private int errorId;

	public ApiError(int errorId) {
		this.errorId = errorId;
	}
}
