package com.project.banking.configuration;

import com.project.banking.repository.AccountRepository;
import com.project.banking.repository.UserRepository;
import com.project.banking.service.AccountService;
import com.project.banking.service.UserService;
import com.project.banking.service.impl.AccountServiceImpl;
import com.project.banking.service.impl.UserServiceImpl;
import com.project.banking.util.DocumentsFunctionality;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeansConfig {
	private AccountRepository accountRepository;
	private UserRepository userRepository;
	private DocumentsFunctionality documentsFunctionality;

	@Autowired
	public BeansConfig(AccountRepository accountRepository, UserRepository userRepository, DocumentsFunctionality documentsFunctionality) {
		this.accountRepository = accountRepository;
		this.userRepository = userRepository;
		this.documentsFunctionality = documentsFunctionality;
	}

	@Bean
	public AccountService accountService() {
		return new AccountServiceImpl(accountRepository, documentsFunctionality);
	}

	@Bean
	public UserService userService() {
		return new UserServiceImpl(userRepository);
	}
}
