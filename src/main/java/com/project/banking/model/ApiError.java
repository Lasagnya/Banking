package com.project.banking.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ApiError {											// TODO
	private int errorId;

	public ApiError(int errorId) {
		this.errorId = errorId;
	}
}
