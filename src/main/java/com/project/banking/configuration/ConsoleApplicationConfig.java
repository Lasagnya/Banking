package com.project.banking.configuration;

import com.project.banking.repository.AccountRepository;
import com.project.banking.repository.BankRepository;
import com.project.banking.repository.UserRepository;
import com.project.banking.service.AccountService;
import com.project.banking.service.UserService;
import com.project.banking.service.impl.AccountServiceImpl;
import com.project.banking.service.impl.BankServiceImpl;
import com.project.banking.service.impl.UserServiceImpl;
import com.project.banking.util.DocumentsFunctionality;
import com.project.banking.util.IsPercentsNeeded;
import com.project.banking.util.SwitchInputMethods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

//@Configuration
//@ComponentScan("com.project.banking")
//@EnableJpaRepositories(basePackages = {"com.project.banking.repository"})
public class ConsoleApplicationConfig {
	private final AccountRepository accountRepository;
	private final UserRepository userRepository;
	private final BankRepository bankRepository;
	private final DocumentsFunctionality documentsFunctionality;

//	@Autowired
	public ConsoleApplicationConfig(AccountRepository accountRepository, UserRepository userRepository, BankRepository bankRepository, DocumentsFunctionality documentsFunctionality) {
		this.accountRepository = accountRepository;
		this.userRepository = userRepository;
		this.bankRepository = bankRepository;
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

	@Bean
	public SwitchInputMethods switchInputMethods() {
		return new SwitchInputMethods(accountService(), new BankServiceImpl(bankRepository), userService());
	}

	@Bean
	public IsPercentsNeeded isPercentsNeeded() {
		return new IsPercentsNeeded(accountService());
	}

//	@Bean
//	public DataSource dataSource() {
//		DriverManagerDataSource dataSource = new DriverManagerDataSource();
//		dataSource.setDriverClassName("org.postgresql.Driver");
//		dataSource.setUrl()
//	}
}
