package com.project.banking.to.front;

import com.project.banking.domain.User;
import lombok.*;
import org.modelmapper.ModelMapper;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class AuthenticationDTO {
	private String username;

	private String password;

	public static AuthenticationDTO build(User user) {
		ModelMapper modelMapper = new ModelMapper();
		return modelMapper.map(user, AuthenticationDTO.class);
	}

	public AuthenticationDTO(User user) {
		this.username = user.getName();
		this.password = new String(user.getBytePasswordHash());
	}
}
