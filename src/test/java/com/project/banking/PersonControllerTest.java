package com.project.banking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.banking.controller.AuthenticationController;
import com.project.banking.domain.User;
import com.project.banking.repository.*;
import com.project.banking.security.JWTFilter;
import com.project.banking.security.JWTUtil;
import com.project.banking.service.*;
import com.project.banking.service.impl.AuthenticationServiceImpl;
import com.project.banking.service.impl.UserDetailsServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Optional;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthenticationController.class)
@ContextConfiguration(classes = BankingApplication.class)
public class PersonControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext context;

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@SpyBean(AuthenticationServiceImpl.class)
	private AuthenticationService authenticationService;

	@SpyBean
	private JWTFilter jwtFilter;

	@SpyBean
	private JWTUtil jwtUtil;

	@SpyBean(UserDetailsServiceImpl.class)
	private UserDetailsService userDetailsService;

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

	private User user = new User(5, "test", "test", "$argon2id$v=19$m=65536,t=1,p=1$Uay8Yi4TRHBBN2ogO1OuJg$CaAzBhuS2Ok3NA/Ufvo/nsuuN+cRCVmL2ZBMa+ufRB0".getBytes(), "ROLE_USER", null, null);
	private User admin = new User(13, "admin", "admin", "$argon2id$v=19$m=65536,t=1,p=1$H/B58oP6eR/vTCUEC+w22A$JuJYfE/UyabnAxVSes5fBqkePglriXeD/zTOnaX6nho".getBytes(), "ROLE_ADMIN", null, null);

	@BeforeEach
	public void setup() {
		mockMvc = MockMvcBuilders
				.webAppContextSetup(context)
				.apply(springSecurity())
				.build();
	}

	@Test
	public void userMethod_successful() throws Exception {
		String jwt = jwtUtil.generateToken("test");
		Mockito.when(userRepository.findByName("test")).thenReturn(Optional.of(user));
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
				.get("/api/person/user")
				.header("Authorization", "Bearer " + jwt)
				.accept(MediaType.APPLICATION_JSON);
		mockMvc.perform(mockRequest)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.username", is("test")))
				.andDo(print());
	}

	@Test
	public void userMethod_UserNotFound() throws Exception {
		String jwt = jwtUtil.generateToken("test");
		Mockito.when(userRepository.findByName("test")).thenReturn(Optional.empty());
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
				.get("/api/person/user")
				.header("Authorization", "Bearer " + jwt)
				.accept(MediaType.APPLICATION_JSON);
		Assertions.assertThrows(UsernameNotFoundException.class, () -> mockMvc.perform(mockRequest));
	}

	@Test
	public void userMethod_withoutAuthorization() throws Exception {
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
				.get("/api/person/user");
		mockMvc.perform(mockRequest)
				.andExpect(status().is4xxClientError())
				.andDo(print());
	}

	@Test
	public void userMethod_invalidToken() throws Exception {
		String jwt = jwtUtil.generateToken("test").substring(1);
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
				.get("/api/person/user")
				.header("Authorization", "Bearer " + jwt)
				.accept(MediaType.APPLICATION_JSON);
		mockMvc.perform(mockRequest)
				.andExpect(status().is4xxClientError())
				.andDo(print());
	}

	@Test
	public void userMethod_invalidRole() throws Exception {
		user.setRole("ROLE_GUEST");
		String jwt = jwtUtil.generateToken("test");
		Mockito.when(userRepository.findByName("test")).thenReturn(Optional.of(user));
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
				.get("/api/person/user")
				.header("Authorization", "Bearer " + jwt)
				.accept(MediaType.APPLICATION_JSON);
		mockMvc.perform(mockRequest)
				.andExpect(status().is4xxClientError())
				.andDo(print());
	}

	@Test
	public void adminMethod_successful() throws Exception {
		String jwt = jwtUtil.generateToken("admin");
		Mockito.when(userRepository.findByName("admin")).thenReturn(Optional.of(admin));
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
				.get("/api/person/admin")
				.header("Authorization", "Bearer " + jwt)
				.accept(MediaType.APPLICATION_JSON);
		mockMvc.perform(mockRequest)
				.andExpect(status().isOk())
				.andExpect(content().string(startsWith("admin: \n")))
				.andDo(print());
	}

	@Test
	public void adminMethod_UserNotFound() throws Exception {
		String jwt = jwtUtil.generateToken("admin");
		Mockito.when(userRepository.findByName("admin")).thenReturn(Optional.empty());
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
				.get("/api/person/admin")
				.header("Authorization", "Bearer " + jwt)
				.accept(MediaType.APPLICATION_JSON);
		Assertions.assertThrows(UsernameNotFoundException.class, () -> mockMvc.perform(mockRequest));
	}

	@Test
	public void adminMethod_withoutAuthorization() throws Exception {
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
				.get("/api/person/admin");
		mockMvc.perform(mockRequest)
				.andExpect(status().is4xxClientError())
				.andDo(print());
	}

	@Test
	public void adminMethod_invalidToken() throws Exception {
		String jwt = jwtUtil.generateToken("admin").substring(1);
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
				.get("/api/person/admin")
				.header("Authorization", "Bearer " + jwt)
				.accept(MediaType.APPLICATION_JSON);
		mockMvc.perform(mockRequest)
				.andExpect(status().is4xxClientError())
				.andDo(print());
	}

	@Test
	public void adminMethod_invalidRole() throws Exception {
		admin.setRole("ROLE_GUEST");
		String jwt = jwtUtil.generateToken("admin");
		Mockito.when(userRepository.findByName("admin")).thenReturn(Optional.of(admin));
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
				.get("/api/person/admin")
				.header("Authorization", "Bearer " + jwt)
				.accept(MediaType.APPLICATION_JSON);
		mockMvc.perform(mockRequest)
				.andExpect(status().is4xxClientError())
				.andDo(print());
	}

	@Test
	public void guestMethod_successful() throws Exception {
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
				.get("/api/person/guest")
				.accept(MediaType.APPLICATION_JSON);
		mockMvc.perform(mockRequest)
				.andExpect(status().isOk())
				.andExpect(content().string(is("test")))
				.andDo(print());
	}
}
