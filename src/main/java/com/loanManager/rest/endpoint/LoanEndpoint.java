package com.loanManager.rest.endpoint;

import java.util.Locale;

import javax.validation.Valid;
import javax.validation.constraints.Min;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.loanManager.rest.domain.FinancialInstrument;
import com.loanManager.rest.domain.Loan;
import com.loanManager.rest.endpoint.error.FinancialInstrumentException;
import com.loanManager.rest.service.LoanService;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * Class responsible for recieving loan requests as JSON, parsing them and
 * pushing further to business logic in Service(s)
 * 
 * @author Rados³aw Ba³trukiewicz
 * 
 */
@RestController
@RequestMapping(produces = { MediaType.APPLICATION_JSON_VALUE})
@Validated
public class LoanEndpoint extends BaseEndpoint {

	private static final Logger log = LoggerFactory.getLogger(LoanEndpoint.class);
	static final int DEFAULT_PAGE_SIZE = 10;
	//static final String HEADER_EXTEND_PERIOD = "extendPeriod";
	static final String HEADER_LOAN_ID = "loanId";

	@Autowired
	LoanService loanService;

	@Autowired
	LoanValidator loanValidator;

	
	@RequestMapping(path = "/v1/loans", method = RequestMethod.GET)
	@ApiOperation(value = "Get all loans", notes = "Returns first N loans specified by the size parameter with page offset specified by page parameter.", response = Page.class)
	public Page<Loan> getAll(
			@ApiParam("The size of the page to be returned") @RequestParam(required = false) Integer size,
			@ApiParam("Zero-based page index") @RequestParam(required = false) Integer page) {

		if (size == null) {
			size = DEFAULT_PAGE_SIZE;
		}
		if (page == null) {
			page = 0;
		}

		@SuppressWarnings("deprecation")
		Pageable pageable = new PageRequest(page, size);
		Page<Loan> loans = loanService.findAll(pageable);
		log.info(messageSource.getMessage("loans.getAllSuccess",null, Locale.getDefault()));
		return loans;
	}

	@RequestMapping(path = "/v1/loan/{id}", method = RequestMethod.GET)
	@ApiOperation(value = "Get loan by id", notes = "Returns loan for id specified.", response = Loan.class)
	@ApiResponses(value = { @ApiResponse(code = 404, message = "Loan not found") })
	public ResponseEntity<Object> get(@ApiParam("Loan id") @PathVariable("id") Long id) {
		Loan loan;
		try {
			loan = loanService.findOne(id);
			log.info(messageSource.getMessage("loans.getOneFound",null, Locale.getDefault()));			
			return (loan == null ? ResponseEntity.status(HttpStatus.NOT_FOUND) : ResponseEntity.ok()).body(loan);
		} catch (FinancialInstrumentException e) {
			log.warn(messageSource.getMessage("loan.notRetrieved",null, Locale.getDefault()));
			return ResponseEntity.badRequest().body("Could not retrive loan with id: " + id);
		}

	}

	@RequestMapping(path = "/v1/loan", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE})
	@ApiOperation(value = "Applies for Loan", notes = "Applies for loan. Returns created loan with id.Loan has max term and max amount."
			+ "Loan is also rejected if max amount is asked and application time is within rejection timeframe.", response = Loan.class)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Succesfully created loan"),
			@ApiResponse(code = 400, message = "Validation criteria is not met")})
	public ResponseEntity<Object> applyForLoan(@Valid @ApiParam(value = "the loan to create", required = true) @RequestBody Loan loan) {
		try {
			loan = loanService.save(loan);
			log.info(messageSource.getMessage("loan.added",null, Locale.getDefault())+" Id:"+loan.getId());
			return ResponseEntity.ok().body(loan);
		} catch (FinancialInstrumentException e) {
			log.warn(messageSource.getMessage("loan.applicationFailed",null, Locale.getDefault()));
			return ResponseEntity.badRequest().build();
		}
	}

	@RequestMapping(path = "/v1/loan/extend", method = RequestMethod.PUT)
	@ApiOperation(value = "Update existing loan by adding new term", notes = "Term must be above 1 and loan must exist. Due date of loan gets updated", response = String.class)
	@ApiResponses(value = { 
			@ApiResponse(code = 200, message = "Succesfully extended loan"),
			@ApiResponse(code = 400, message = "Wrong type of parameters are supplied.Loan id and term must be above 1"),
			@ApiResponse(code = 404, message = "Loan with this id is not found") ,
			@ApiResponse(code = 304, message = "Loan was found but could not extend")})
	public ResponseEntity<Object> extend(@Min(1)  @ApiParam(value = "loanId", required = true) @RequestHeader(name = HEADER_LOAN_ID, required = true) Long loanId)
			//,@Min(1)  @ApiParam(value = "extendPeriod", required = true, defaultValue = "1") @RequestHeader(name = HEADER_EXTEND_PERIOD, required = true) Long extendPeriod)
	{
		FinancialInstrument loan;
		try {
			loan = loanService.findOne(loanId);	
			log.info(messageSource.getMessage("loan.found",null, Locale.getDefault()) + " Id:"+ loanId);
		} catch (FinancialInstrumentException e) {
			log.warn(messageSource.getMessage("loan.notFound",null, Locale.getDefault()) + " Id:"+ loanId);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		try {
			loanService.extend(loan);//, extendPeriod);
		} catch (FinancialInstrumentException e) {
			log.warn(messageSource.getMessage("loan.notExtended",null, Locale.getDefault()) + " Id:"+ loanId);
			return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
		}
		String messageSuccess = messageSource.getMessage("loan.extended",null, Locale.getDefault())+ " Id:"+ loanId;
		log.info(messageSuccess);
		return ResponseEntity.ok().body(messageSuccess);
	}

	@InitBinder("loan")
	protected void initBinder(WebDataBinder binder) {
		binder.addValidators(loanValidator);
	}
}