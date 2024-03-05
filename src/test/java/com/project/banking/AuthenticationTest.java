package com.project.banking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.banking.controller.AuthenticationController;
import com.project.banking.repository.*;
import com.project.banking.security.JWTFilter;
import com.project.banking.security.JWTUtil;
import com.project.banking.service.*;
import com.project.banking.service.impl.AuthenticationServiceImpl;
import com.project.banking.to.front.AuthenticationDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.*;

@WebMvcTest(AuthenticationController.class)
@ContextConfiguration(classes = BankingApplication.class)
public class AuthenticationTest {
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper mapper;

	@SpyBean(AuthenticationServiceImpl.class)
	private AuthenticationService authenticationService;

	@SpyBean
	private JWTFilter jwtFilter;

	@SpyBean
	private JWTUtil jwtUtil;

	@MockBean
	private UserRepository userRepository;

	@MockBean
	private AuthenticationManager authenticationManager;

	@MockBean
	private AccountService accountService;

	@MockBean
	private BankService bankService;

	@MockBean
	private TransactionService transactionService;

	@MockBean
	private TransactionCallbackService transactionCallbackService;

	@Autowired
	private WebApplicationContext context;

	@BeforeEach
	public void setup() {
		mockMvc = MockMvcBuilders
				.webAppContextSetup(context)
				.apply(springSecurity())
				.build();
	}

	@Test
	public void login_successful() throws Exception {
		AuthenticationDTO auth = new AuthenticationDTO("test", "test");
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
				.post("/api/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("utf-8")
				.accept(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(auth));
		mockMvc.perform(mockRequest)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.jwt-token", is(notNullValue())))
				.andDo(print());
	}

	@Test
	public void login_unsuccessful() throws Exception {
		AuthenticationDTO auth = new AuthenticationDTO("test", "test");
		Mockito.when(authenticationManager.authenticate(ArgumentMatchers.any())).thenThrow(new BadCredentialsException("Incorrect password"));
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
				.post("/api/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("utf-8")
				.accept(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(auth));
		mockMvc.perform(mockRequest)
				.andExpect(status().isUnauthorized())
				.andDo(print());
	}
}
