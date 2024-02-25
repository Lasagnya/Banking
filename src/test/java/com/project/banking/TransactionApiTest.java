package com.project.banking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.banking.client.CallbackClient;
import com.project.banking.security.JWTFilter;
import com.project.banking.controller.TransactionAPI;
import com.project.banking.enumeration.Currency;
import com.project.banking.enumeration.TransactionStatus;
import com.project.banking.enumeration.TypeOfTransaction;
import com.project.banking.domain.Transaction;
import com.project.banking.exception.IncorrectAmountException;
import com.project.banking.exception.IncorrectBankException;
import com.project.banking.exception.IncorrectReceivingAccountException;
import com.project.banking.repository.*;
import com.project.banking.security.JWTUtil;
import com.project.banking.service.*;
import com.project.banking.service.impl.AccountServiceImpl;
import com.project.banking.service.impl.TransactionCallbackServiceImpl;
import com.project.banking.service.impl.TransactionServiceImpl;
import com.project.banking.to.client.TransactionIncoming;
import com.project.banking.util.ConfirmationCodeFunctionality;
import com.project.banking.util.TransactionVerification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Date;
import java.util.Optional;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.*;

@WebMvcTest(TransactionAPI.class)
public class TransactionApiTest {
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper mapper;

	@SpyBean(TransactionServiceImpl.class)
	private TransactionService transactionService;

	@SpyBean(TransactionCallbackServiceImpl.class)
	private TransactionCallbackService transactionCallbackService;

	@SpyBean(AccountServiceImpl.class)
	private AccountService accountService;

	@SpyBean
	private JWTFilter jwtFilter;

	@SpyBean
	private JWTUtil jwtUtil;

	@MockBean
	private TransactionRepository transactionRepository;

	@MockBean
	private AccountRepository accountRepository;

	@MockBean
	private BankRepository bankRepository;

	@MockBean
	private UserRepository userRepository;

	@MockBean
	private TransactionCallbackRepository transactionCallbackRepository;

	@MockBean
	private TransactionVerification transactionVerification;

	@MockBean
	private ConfirmationCodeFunctionality confirmationCode;

	@MockBean
	private CallbackClient callbackClient;

	private final TransactionIncoming transactionIncoming = new TransactionIncoming(22, 1, 1234, 12345678, 100.0, Currency.BYN, "http://test");

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
	public void makeTransaction_succeed() throws Exception {
		Transaction transaction = new Transaction(transactionIncoming);
		transaction.setId(0);
		Mockito.when(transactionRepository.save(ArgumentMatchers.any())).thenReturn(transaction);
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
				.post("/api/transaction/pay")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(transactionIncoming));
		mockMvc.perform(mockRequest)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.status", is(TransactionStatus.PENDING.toString())))
				.andExpect(jsonPath("$.invoiceId", is(transactionIncoming.getInvoiceId())))
				.andExpect(jsonPath("$.amount", is(transactionIncoming.getAmount())))
				.andDo(print());
	}

	@Test
	public void makeTransaction_incorrectBank() throws Exception {
		Mockito.doThrow(new IncorrectBankException()).when(transactionVerification).verify(transactionIncoming);
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
				.post("/api/transaction/pay")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(transactionIncoming));
		mockMvc.perform(mockRequest)
				.andExpect(status().isConflict())
				.andExpect(jsonPath("$.status", is(TransactionStatus.INVALID.toString())))
				.andExpect(jsonPath("$.invoiceId", is(transactionIncoming.getInvoiceId())))
				.andExpect(jsonPath("$.amount", is(transactionIncoming.getAmount())))
				.andDo(print());
	}

	@Test
	public void makeTransaction_incorrectAccount() throws Exception {
		Mockito.doThrow(new IncorrectReceivingAccountException()).when(transactionVerification).verify(transactionIncoming);
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
				.post("/api/transaction/pay")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(transactionIncoming));
		mockMvc.perform(mockRequest)
				.andExpect(status().isConflict())
				.andExpect(jsonPath("$.status", is(TransactionStatus.INVALID.toString())))
				.andExpect(jsonPath("$.invoiceId", is(transactionIncoming.getInvoiceId())))
				.andExpect(jsonPath("$.amount", is(transactionIncoming.getAmount())))
				.andDo(print());
	}

	@Test
	public void makeTransaction_incorrectAmount() throws Exception {
		Transaction transaction = new Transaction(transactionIncoming);
		transaction.setId(0);
		Mockito.doThrow(new IncorrectAmountException()).when(transactionVerification).verify(transactionIncoming);
		Mockito.when(transactionRepository.save(ArgumentMatchers.any())).thenReturn(transaction);
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
				.post("/api/transaction/pay")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(transactionIncoming));
		mockMvc.perform(mockRequest)
				.andExpect(status().isConflict())
				.andExpect(jsonPath("$.status", is(TransactionStatus.INVALID.toString())))
				.andExpect(jsonPath("$.amount", is(transactionIncoming.getAmount())))
				.andDo(print());
	}

	@Test
	public void finaliseTransaction_success() throws Exception {
		Transaction transaction = new Transaction(transactionIncoming);
		transaction.setId(25);
		transaction.setConfirmationCode(1);
		Mockito.when(transactionRepository.findById(transaction.getId())).thenReturn(Optional.of(transaction));
		Mockito.when(confirmationCode.verifyConfirmationCode(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(true);
		Mockito.when(transactionCallbackRepository.findById(transaction.getId())).thenReturn(Optional.of(transaction.getClientInformation()));
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
				.post("/api/transaction/confirming")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(transaction));
		mockMvc.perform(mockRequest)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(transaction.getId())))
				.andExpect(jsonPath("$.status", is(TransactionStatus.PAID.toString())))
				.andExpect(jsonPath("$.amount", is(transactionIncoming.getAmount())))
				.andDo(print());
	}

	@Test
	public void finaliseTransaction_Expired() throws Exception {
		Transaction transaction = new Transaction(25, new Date(), TypeOfTransaction.TRANSFER, 1, 1, 1234, 12345678, 100.0, Currency.BYN, TransactionStatus.EXPIRED, 1);
		Mockito.when(transactionRepository.findById(transaction.getId())).thenReturn(Optional.of(transaction));
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
				.post("/api/transaction/confirming")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(transaction));
		mockMvc.perform(mockRequest)
				.andExpect(status().isConflict())
				.andDo(print());
	}

	@Test
	public void finaliseTransaction_AlreadyInvalid() throws Exception {
		Transaction transaction = new Transaction(25, new Date(), TypeOfTransaction.TRANSFER, 1, 1, 1234, 12345678, 100.0, Currency.BYN, TransactionStatus.INVALID, 1);
		Mockito.when(transactionRepository.findById(transaction.getId())).thenReturn(Optional.of(transaction));
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
				.post("/api/transaction/confirming")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(transaction));
		mockMvc.perform(mockRequest)
				.andExpect(status().is4xxClientError())
				.andDo(print());
	}

	@Test
	public void finaliseTransaction_IncorrectCode() throws Exception {
		Transaction transaction = new Transaction(25, new Date(), TypeOfTransaction.TRANSFER, 1, 1, 1234, 12345678, 100.0, Currency.BYN, TransactionStatus.PENDING, 1);
		Mockito.when(transactionRepository.findById(transaction.getId())).thenReturn(Optional.of(transaction));
		Mockito.when(confirmationCode.verifyConfirmationCode(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(false);
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
				.post("/api/transaction/confirming")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(transaction));
		mockMvc.perform(mockRequest)
				.andExpect(status().isConflict())
				.andDo(print());
	}

	@Test
	public void finaliseTransaction_IncorrectId() throws Exception {
		Transaction transaction = new Transaction(25, new Date(), TypeOfTransaction.TRANSFER, 1, 1, 1234, 12345678, 100.0, Currency.BYN, TransactionStatus.PENDING, 1);
		Mockito.when(transactionRepository.findById(transaction.getId())).thenReturn(Optional.empty());
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
				.post("/api/transaction/confirming")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(transaction));
		mockMvc.perform(mockRequest)
				.andExpect(status().is4xxClientError())
				.andDo(print());
	}
}
