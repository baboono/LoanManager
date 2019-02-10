package com.loanManager.rest.endpoint;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.NestedServletException;

import com.loanManager.rest.domain.Loan;
import com.loanManager.rest.service.LoanService;

/**
 * Main endpoint mocked tests
 * 
 * @author Rados³aw Ba³trukiewicz
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@Transactional
public class LoanEndpointMockedTest extends BaseEndpointTest {

	@MockBean
	private LoanService loanService;

	private Loan testLoan;

	@Before
	public void setup() throws Exception {
		super.setup();
		testLoan = new Loan(12L, BigDecimal.ONE);
		when(loanService.findOne(1L)).thenReturn(testLoan);
		when(loanService.save(testLoan)).thenReturn(testLoan);
	}

	@Test
	public void getLoanById() throws Exception {

		mockMvc.perform(get("/v1/loan/{id}", 1)).andDo(print()).andExpect(status().isOk())
				.andExpect(content().contentType(JSON_MEDIA_TYPE)).andExpect(jsonPath("$.term", is(12)))
				.andExpect(jsonPath("$.amount", is(1)));
	}

	@Test
	public void createLoan() throws Exception {
		testLoan = new Loan(12L, BigDecimal.ONE);
		mockMvc.perform(post("/v1/loan").contentType(JSON_MEDIA_TYPE).content(json(testLoan))).andDo(print())
				.andExpect(status().isOk()).andExpect(content().contentType(JSON_MEDIA_TYPE))
				.andExpect(jsonPath("$.term", is(12))).andExpect(jsonPath("$.amount", is(1)));
	}

	@Test
	public void extendLoan() throws Exception {
		testLoan = new Loan(12L, BigDecimal.ONE);
		mockMvc.perform(put("/v1/loan/extend").contentType(JSON_MEDIA_TYPE).header("loanId", "1")
				.header("extendPeriod", "10")).andDo(print()).andExpect(status().isOk());
	}

	@Test(expected = NestedServletException.class)
	public void handleGenericException() throws Exception {

		when(loanService.findOne(1L)).thenThrow(new RuntimeException("Failed to get loan by id"));

		mockMvc.perform(get("/v1/loan/{id}", 1)).andDo(print()).andExpect(status().is5xxServerError())
				.andExpect(content().string(""));
	}
}
