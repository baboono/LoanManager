package com.loanManager.rest.endpoint;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

import javax.persistence.EntityManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.loanManager.rest.config.ApplicationProperties;
import com.loanManager.rest.domain.Loan;
import com.loanManager.rest.service.LoanService;
/**
 * Main test class
 * 
 * @author Rados³aw Ba³trukiewicz
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@Transactional
public class LoanEndpointTest extends BaseEndpointTest {

	@Autowired
	private EntityManager entityManager;
	
	@Autowired
	private LoanService loanService;
	
	@Autowired
	private ApplicationProperties applicationProperties;
	
	private Loan testLoan;
	
    @Before
    public void setup() throws Exception {
    	super.setup();

    	// create test loans
    	loanService.save(createLoan(100L,new BigDecimal(11.5)));
    	loanService.save(createLoan(1000L,new BigDecimal(30)));
    	loanService.save(createLoan(500L,new BigDecimal(20)));
    	@SuppressWarnings("deprecation")
		Page<Loan> loansPage = loanService.findAll(new PageRequest(0, LoanEndpoint.DEFAULT_PAGE_SIZE));
		assertNotNull(loansPage);
		assertEquals(3L, loansPage.getTotalElements());
		testLoan = loansPage.getContent().get(0);
		assertNotNull(testLoan);
		entityManager.refresh(testLoan);
    }
    /**
     * Test get loan by ID
     */
    @Test
    public void getLoanById() throws Exception {
    	Loan testLoan = loanService.save(createLoan(10L,new BigDecimal(11.5)));
		mockMvc.perform(get("/v1/loan/{id}", testLoan.getId())).andDo(print()).andExpect(status().isOk())
    	.andDo(mvcResult -> {
            String json = mvcResult.getResponse().getContentAsString();
            Loan returnedLoan = (Loan) convertJSONStringToObject(json);
            if(!returnedLoan.getAmount().equals(new BigDecimal(11.5)))
            fail("returned amount does not match"+returnedLoan.toString());
            if(!returnedLoan.getTerm().equals(10L))
            fail("returned term does not match"+returnedLoan.toString());
            if(!returnedLoan.getTermUnit().equals(applicationProperties.getLoanMaxTermUnit()))
            fail("returned term unit does not match"+returnedLoan.toString());
            if(!returnedLoan.getCost().equals(returnedLoan.getAmount().multiply(applicationProperties.getLoanCostMultipier())))
            fail("returned cost does not match"+returnedLoan.toString());
            LocalDateTime createdLoanDueTime = LocalDateTime.ofInstant(returnedLoan.getDueDate().toInstant(), ZoneId.systemDefault());
            LocalDateTime dueDateFromNow = LocalDateTime.now().plusDays(returnedLoan.getTerm());
            if(!createdLoanDueTime.isBefore(dueDateFromNow))
            fail("Created date time does not match"+createdLoanDueTime);
            if(!createdLoanDueTime.isAfter(dueDateFromNow.minusSeconds(30)))
            fail("Created date time does not match"+createdLoanDueTime);
    	});

    	
    }

    /**
     * Test create loan success. 
     */
    @Test
    public void createLoanReturnedSuccess() throws Exception {
    	
    	testLoan = new Loan(1L, BigDecimal.ONE);
		mockMvc.perform(post("/v1/loan").contentType(JSON_MEDIA_TYPE).content(json(testLoan))).andDo(print())
		.andExpect(status().isOk())
    	.andDo(mvcResult -> {
            String json = mvcResult.getResponse().getContentAsString();
            Loan returnedLoan = (Loan) convertJSONStringToObject(json);
            if(!returnedLoan.getAmount().equals(new BigDecimal(1)))
            fail("returned amount does not match"+returnedLoan.toString());
            if(!returnedLoan.getTerm().equals(1L))
            fail("returned term does not match"+returnedLoan.toString());
            if(!returnedLoan.getTermUnit().equals(applicationProperties.getLoanMaxTermUnit()))
            fail("returned term unit does not match"+returnedLoan.toString());
            if(!returnedLoan.getCost().equals(returnedLoan.getAmount().multiply(applicationProperties.getLoanCostMultipier())))
            fail("returned cost does not match"+returnedLoan.toString());
            LocalDateTime createdLoanDueTime = LocalDateTime.ofInstant(returnedLoan.getDueDate().toInstant(), ZoneId.systemDefault());
            LocalDateTime dueDateTimeFromNow = LocalDateTime.now().plusDays(returnedLoan.getTerm());
            if(!createdLoanDueTime.isBefore(dueDateTimeFromNow))
            fail("Created date time does not match"+createdLoanDueTime);
            if(!createdLoanDueTime.isAfter(dueDateTimeFromNow.minusSeconds(30)))
            fail("Created date time does not match"+createdLoanDueTime);
    	});

    }

    /**
     * Test extend loan success. 
     */
    @Test
    public void createLoanExtendedSuccess() throws Exception {
    	Loan testLoan = loanService.save(createLoan(10L,new BigDecimal(11.5)));
		mockMvc.perform(put("/v1/loan/extend").contentType(JSON_MEDIA_TYPE).header("loanId",testLoan.getId())).andDo(print())
		.andExpect(status().isOk());		
		mockMvc.perform(get("/v1/loan/{id}", testLoan.getId())).andDo(print()).andExpect(status().isOk())
    	.andDo(mvcResult -> {
            String json = mvcResult.getResponse().getContentAsString();
            Loan returnedLoan = (Loan) convertJSONStringToObject(json);
            LocalDateTime createdLoanDueTime = LocalDateTime.ofInstant(returnedLoan.getDueDate().toInstant(), ZoneId.systemDefault());
            LocalDateTime dueFromNow = LocalDateTime.now().plusDays(returnedLoan.getTerm() + applicationProperties.getDefaultExtendTerm());
            if(!createdLoanDueTime.getMonth().equals(dueFromNow.getMonth()))
            fail("Created date time MONTH not match"+createdLoanDueTime);
            if(!(createdLoanDueTime.getYear()==dueFromNow.getYear()))
            fail("Created date time YEAR not match"+createdLoanDueTime);
            if(!(createdLoanDueTime.getDayOfMonth()==dueFromNow.getDayOfMonth()))
            fail("Created date time DAY not match"+createdLoanDueTime);
    	});
		
		
		
    }

    /**
     * Test application is between current server timeframe and max amount is asked
     */
    @Test
    public void createLoanMaxAmountAndServerTime() throws Exception {
    	LocalTime localTime = LocalTime.now();
    	applicationProperties.setLoanMinRejectTime(localTime.toString());
    	applicationProperties.setLoanMaxRejectTime(localTime.plusHours(1).toString());
    	testLoan = new Loan(1L, applicationProperties.getLoanMaxAmount());
		mockMvc.perform(post("/v1/loan").contentType(JSON_MEDIA_TYPE).content(json(testLoan))).andDo(print())
		.andExpect(status().isBadRequest());
    }
    /**
     * Test application is not within amount range
     */
    
    @Test
    public void createLoanNotWithinAmount() throws Exception {
    	testLoan = new Loan(1L, applicationProperties.getLoanMaxAmount().add(BigDecimal.ONE));
		mockMvc.perform(post("/v1/loan").contentType(JSON_MEDIA_TYPE).content(json(testLoan))).andDo(print())
		.andExpect(status().isBadRequest());
    }
    /**
     * Test application is not within term range
     */
    @Test
    public void createLoanNotWithinTerm() throws Exception {
    	
    	testLoan = new Loan(applicationProperties.getLoanMaxTerm() +1L,BigDecimal.ONE);
		mockMvc.perform(post("/v1/loan").contentType(JSON_MEDIA_TYPE).content(json(testLoan))).andDo(print())
		.andExpect(status().isBadRequest());
    }
    /**
     * Test not supported method
     */
    @Test
    public void handleHttpRequestMethodNotSupportedException() throws Exception {
    	
    	String content = json(testLoan);
    	
    	mockMvc.perform(
    			delete("/v1/loan") //not supported method
    			.header(LoanEndpoint.HEADER_LOAN_ID, UUID.randomUUID())
    			.accept(JSON_MEDIA_TYPE)
    			.content(content)
    			.contentType(JSON_MEDIA_TYPE))
    	.andDo(print())
    	.andExpect(status().isMethodNotAllowed())
    	.andExpect(content().string(""))
    	;
    }

	private Loan createLoan(Long term, BigDecimal amount) {
		Loan loan = new Loan(term,amount);
		loan.setDueDate(new Date());
		return loan;
	}

}
