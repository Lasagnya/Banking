package com.project.banking.to.front;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ApiError {													// TODO сделать полноценную реализацию
	private int errorId;

	public ApiError(int errorId) {
		this.errorId = errorId;
	}
}
