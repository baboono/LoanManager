package com.loanManager.rest.service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.loanManager.enums.TermUnit;
import com.loanManager.rest.config.ApplicationProperties;
import com.loanManager.rest.domain.FinancialInstrument;
import com.loanManager.rest.domain.Loan;
import com.loanManager.rest.endpoint.error.FinancialInstrumentException;
import com.loanManager.rest.repository.LoanRepository;

/**
 * Service involved for all loan entity based operations
 * 
 * @author Rados³aw Ba³trukiewicz
 * 
 */
@Service
@Transactional
public class LoanService extends FinancialInstrumentService {

	@Autowired
	private LoanRepository repository;
	@Autowired
	private ApplicationProperties applicationProperties;

	@Override
	@Transactional(readOnly = true)
	public Page<Loan> findAll(Pageable pageable) {
		return repository.findAll(pageable);
	}

	@Override
	@Transactional(readOnly = true)
	public Loan findOne(Long id) throws FinancialInstrumentException {
		try {
			Hibernate.initialize(repository.getOne(id));
			return repository.getOne(id);
		} catch (Exception e) {
			throw new FinancialInstrumentException("Could not get loan from database due to Hibernate exception: " +e.getMessage());
		}
	}

	@Override
	@Transactional
	public Loan save(FinancialInstrument loan) throws FinancialInstrumentException {
		Loan loanInstance = getInstance(loan);
		loan = setupNewLoan(loanInstance);
		try {
			 loanInstance = repository.saveAndFlush(loanInstance);			 
			return loanInstance;
		} catch (Exception e) {
			throw new FinancialInstrumentException("Could not persist loan to database. Exception is: " +e.getMessage());
		}
	}
	@Override
	@Transactional
	public Loan update(FinancialInstrument loan) throws FinancialInstrumentException {
		try {
			return repository.saveAndFlush(getInstance(loan));
		} catch (Exception e) {
			throw new FinancialInstrumentException("Could not persist loan to database. Exception is: " +e.getMessage());
		}
	}

	@Override
	public void extend(FinancialInstrument financialInstrument) throws FinancialInstrumentException {
		Loan loan = getInstance(financialInstrument);
		TermUnit termUnit = loan.getTermUnit();
		ZonedDateTime localDateTime = adjustDate(financialInstrument.getDueDate(),applicationProperties.getDefaultExtendTerm(),termUnit);
		Date dueDate = Date
				.from(ZonedDateTime.parse(localDateTime.toString()).toInstant());
		loan.setDueDate(dueDate);
		update(loan);
		
	}
	
	private ZonedDateTime adjustDate(Date dueDate,Long term, TermUnit termUnit) {
		ZonedDateTime localDateTime;
		if(dueDate==null) {
		localDateTime = ZonedDateTime.now();
		}
		else {
			localDateTime = ZonedDateTime.ofInstant(dueDate.toInstant(),
                    ZoneId.systemDefault());
		}
		switch (termUnit) {
		case DAY:
			localDateTime = localDateTime.plusDays(term);
			break;
		case MONTH:
			localDateTime = localDateTime.plusMonths(term);
			break;
		case YEAR:
			localDateTime = localDateTime.plusYears(term);
			break;
		default:
			localDateTime = localDateTime.plusDays(term);
			break;
		}
		return localDateTime;
		
	}
	
	private Loan setupNewLoan(Loan loan) {
		loan.setCost(loan.getAmount().multiply(getLoansCostMultiplier()));
		TermUnit termUnit = getDefaultLoansMaxTermUnit();
		loan.setTermUnit(termUnit);
		long term = loan.getTerm();
		ZonedDateTime localDateTime = adjustDate(null,term,termUnit );
		Date dueDate = Date
				.from(ZonedDateTime.parse(localDateTime.toString()).toInstant());
		loan.setDueDate(dueDate);
		return loan;

	}
	
	private Loan getInstance(FinancialInstrument financialInstrument) {
		try {
			return (Loan) financialInstrument;
		} catch (ClassCastException e) {
			throw new RuntimeException("Cannot retrive loan from financial instrument", e);
		}
	}

}
