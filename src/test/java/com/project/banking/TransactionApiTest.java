package com.project.banking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.banking.models.Currency;
import com.project.banking.models.TransactionIncoming;
import com.project.banking.models.TransactionStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.*;

@SpringBootTest()
@AutoConfigureMockMvc
public class TransactionApiTest {
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper mapper;

	@Test
	public void makeTransaction_succeed() throws Exception {
		TransactionIncoming transactionIncoming = new TransactionIncoming(22, 1, 1234, 12345678, 100.0, Currency.BYN, "http://test");
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
				.post("/api/transaction/pay")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(transactionIncoming));
		mockMvc.perform(mockRequest)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.status", is(TransactionStatus.PENDING.toString())))
				.andExpect(jsonPath("$.invoiceId", is(22)))
				.andExpect(jsonPath("$.amount", is(100.0)));
	}

	@Test
	public void makeTransaction_incorrectBank() throws Exception {
		TransactionIncoming transactionIncoming = new TransactionIncoming(22, 5, 1234, 12345678, 100.0, Currency.BYN, "http://test");
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
				.post("/api/transaction/pay")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(transactionIncoming));
		mockMvc.perform(mockRequest)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.status", is(TransactionStatus.INVALID.toString())));
	}

	@Test
	public void makeTransaction_incorrectAccount() throws Exception {
		TransactionIncoming transactionIncoming = new TransactionIncoming(22, 1, 12345, 12345678, 100.0, Currency.BYN, "http://test");
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
				.post("/api/transaction/pay")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(transactionIncoming));
		mockMvc.perform(mockRequest)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.status", is(TransactionStatus.INVALID.toString())));
	}

	@Test
	public void makeTransaction_incorrectAmount() throws Exception {
		TransactionIncoming transactionIncoming = new TransactionIncoming(22, 5, 1234, 12345678, 100.0, Currency.BYN, "http://test");
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
				.post("/api/transaction/pay")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(transactionIncoming));
		mockMvc.perform(mockRequest)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.status", is(TransactionStatus.INVALID.toString())))
				.andExpect(jsonPath("$.invoiceId", is(22)))
				.andExpect(jsonPath("$.amount", is(100.0)));
	}
}
