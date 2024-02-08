package com.project.banking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.banking.client.CallbackClient;
import com.project.banking.controller.TransactionAPI;
import com.project.banking.dao.*;
import com.project.banking.enumeration.Currency;
import com.project.banking.enumeration.TransactionStatus;
import com.project.banking.enumeration.TypeOfTransaction;
import com.project.banking.model.*;
import com.project.banking.model.database.TransactionDb;
import com.project.banking.service.*;
import com.project.banking.service.impl.TransactionCallbackServiceImpl;
import com.project.banking.service.impl.TransactionServiceImpl;
import com.project.banking.util.ConfirmationCodeFunctionality;
import com.project.banking.util.TransactionVerification;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Date;
import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.*;

@WebMvcTest(TransactionAPI.class)
@AutoConfigureMockMvc
public class TransactionApiTest {
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper mapper;

	@SpyBean(TransactionServiceImpl.class)
	private TransactionService transactionService;

	@SpyBean(TransactionCallbackServiceImpl.class)
	private TransactionCallbackService transactionCallbackService;

	@MockBean
	private TransactionDAO transactionDAO;

	@MockBean
	private AccountDAO accountDAO;

	@MockBean
	private BankDAO bankDAO;

	@MockBean
	private UserDAO userDAO;

	@MockBean
	private TransactionCallbackDAO transactionCallbackDAO;

	@MockBean
	private TransactionVerification transactionVerification;

	@MockBean
	private ConfirmationCodeFunctionality confirmationCode;

	@MockBean
	private CallbackClient callbackClient;

	private final TransactionIncoming transactionIncoming = new TransactionIncoming(22, 1, 1234, 12345678, 100.0, Currency.BYN, "http://test");

	@Test
	public void makeTransaction_succeed() throws Exception {
		TransactionDb transaction = new TransactionDb(transactionIncoming);
		transaction.setId(0);
		Mockito.when(transactionVerification.verify(transactionIncoming)).thenReturn(0);
		Mockito.when(transactionDAO.saveTransaction(ArgumentMatchers.any())).thenReturn(transaction);
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
		Mockito.when(transactionVerification.verify(transactionIncoming)).thenReturn(1);
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
				.post("/api/transaction/pay")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(transactionIncoming));
		mockMvc.perform(mockRequest)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.status", is(TransactionStatus.INVALID.toString())))
				.andExpect(jsonPath("$.invoiceId", is(transactionIncoming.getInvoiceId())))
				.andExpect(jsonPath("$.amount", is(transactionIncoming.getAmount())))
				.andDo(print());
	}

	@Test
	public void makeTransaction_incorrectAccount() throws Exception {
		Mockito.when(transactionVerification.verify(transactionIncoming)).thenReturn(10);
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
				.post("/api/transaction/pay")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(transactionIncoming));
		mockMvc.perform(mockRequest)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.status", is(TransactionStatus.INVALID.toString())))
				.andExpect(jsonPath("$.invoiceId", is(transactionIncoming.getInvoiceId())))
				.andExpect(jsonPath("$.amount", is(transactionIncoming.getAmount())))
				.andDo(print());
	}

	@Test
	public void makeTransaction_incorrectAmount() throws Exception {
		TransactionDb transaction = new TransactionDb(transactionIncoming);
		transaction.setId(0);
		Mockito.when(transactionVerification.verify(transactionIncoming)).thenReturn(1000);
		Mockito.when(transactionDAO.saveTransaction(ArgumentMatchers.any())).thenReturn(transaction);
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
				.post("/api/transaction/pay")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(transactionIncoming));
		mockMvc.perform(mockRequest)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.status", is(TransactionStatus.INVALID.toString())))
				.andExpect(jsonPath("$.amount", is(transactionIncoming.getAmount())))
				.andDo(print());
	}

	@Test
	public void finaliseTransaction_success() throws Exception {
		TransactionDb transaction = new TransactionDb(25, new Date(), TypeOfTransaction.TRANSFER, 1, 1, 1234, 12345678, 100.0, Currency.BYN, TransactionStatus.PENDING, 1);
		TransactionCallback callback = new TransactionCallback(transaction, transactionIncoming);
		Mockito.when(transactionDAO.findById(transaction.getId())).thenReturn(Optional.of(transaction));
		Mockito.when(confirmationCode.verifyConfirmationCode(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(true);
		Mockito.when(transactionCallbackDAO.findById(transaction.getId())).thenReturn(Optional.of(callback));
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
				.post("/api/transaction/confirming")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(transaction));
		mockMvc.perform(mockRequest)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.transaction.id", is(transaction.getId())))
				.andExpect(jsonPath("$.transaction.status", is(TransactionStatus.PAID.toString())))
				.andExpect(jsonPath("$.transaction.amount", is(transactionIncoming.getAmount())))
				.andExpect(jsonPath("$.apiError.errorId", is(0)))
				.andDo(print());
	}

	@Test
	public void finaliseTransaction_Expired() throws Exception {
		TransactionDb transaction = new TransactionDb(25, new Date(), TypeOfTransaction.TRANSFER, 1, 1, 1234, 12345678, 100.0, Currency.BYN, TransactionStatus.EXPIRED, 1);
		Mockito.when(transactionDAO.findById(transaction.getId())).thenReturn(Optional.of(transaction));
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
				.post("/api/transaction/confirming")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(transaction));
		mockMvc.perform(mockRequest)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.transaction.id", is(transaction.getId())))
				.andExpect(jsonPath("$.transaction.status", is(TransactionStatus.EXPIRED.toString())))
				.andExpect(jsonPath("$.transaction.amount", is(transactionIncoming.getAmount())))
				.andExpect(jsonPath("$.apiError.errorId", is(3)))
				.andDo(print());
	}

	@Test
	public void finaliseTransaction_Invalid() throws Exception {
		TransactionDb transaction = new TransactionDb(25, new Date(), TypeOfTransaction.TRANSFER, 1, 1, 1234, 12345678, 100.0, Currency.BYN, TransactionStatus.INVALID, 1);
		Mockito.when(transactionDAO.findById(transaction.getId())).thenReturn(Optional.of(transaction));
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
				.post("/api/transaction/confirming")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(transaction));
		mockMvc.perform(mockRequest)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.transaction.id", is(transaction.getId())))
				.andExpect(jsonPath("$.transaction.status", is(TransactionStatus.INVALID.toString())))
				.andExpect(jsonPath("$.transaction.amount", is(transactionIncoming.getAmount())))
				.andExpect(jsonPath("$.apiError.errorId", is(4)))
				.andDo(print());
	}

	@Test
	public void finaliseTransaction_IncorrectCode() throws Exception {
		TransactionDb transaction = new TransactionDb(25, new Date(), TypeOfTransaction.TRANSFER, 1, 1, 1234, 12345678, 100.0, Currency.BYN, TransactionStatus.PENDING, 1);
		Mockito.when(transactionDAO.findById(transaction.getId())).thenReturn(Optional.of(transaction));
		Mockito.when(confirmationCode.verifyConfirmationCode(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(false);
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
				.post("/api/transaction/confirming")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(transaction));
		mockMvc.perform(mockRequest)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.transaction.id", is(transaction.getId())))
				.andExpect(jsonPath("$.transaction.status", is(TransactionStatus.PENDING.toString())))
				.andExpect(jsonPath("$.transaction.amount", is(transactionIncoming.getAmount())))
				.andExpect(jsonPath("$.apiError.errorId", is(2)))
				.andDo(print());
	}

	@Test
	public void finaliseTransaction_IncorrectId() throws Exception {
		TransactionDb transaction = new TransactionDb(25, new Date(), TypeOfTransaction.TRANSFER, 1, 1, 1234, 12345678, 100.0, Currency.BYN, TransactionStatus.PENDING, 1);
		Mockito.when(transactionDAO.findById(transaction.getId())).thenReturn(Optional.empty());
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
				.post("/api/transaction/confirming")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(transaction));
		mockMvc.perform(mockRequest)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.transaction.id", is(transaction.getId())))
				.andExpect(jsonPath("$.transaction.status", is(transaction.getStatus().toString())))
				.andExpect(jsonPath("$.transaction.amount", is(transactionIncoming.getAmount())))
				.andExpect(jsonPath("$.apiError.errorId", is(1)))
				.andDo(print());
	}
}
