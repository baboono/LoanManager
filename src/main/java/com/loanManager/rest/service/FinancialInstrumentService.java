package com.loanManager.rest.service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.loanManager.enums.TermUnit;
import com.loanManager.rest.config.ApplicationProperties;
import com.loanManager.rest.domain.FinancialInstrument;
import com.loanManager.rest.endpoint.error.FinancialInstrumentException;



public abstract class FinancialInstrumentService {
	@Autowired
    private ApplicationProperties applicationProperties;

	abstract Page<?> findAll(Pageable pageable);
	
	abstract FinancialInstrument save(FinancialInstrument financialInstrument) throws FinancialInstrumentException;

	abstract FinancialInstrument findOne(Long id) throws FinancialInstrumentException;

	abstract void extend(FinancialInstrument financialInstrument) throws FinancialInstrumentException;
	
	abstract FinancialInstrument update(FinancialInstrument financialInstrument) throws FinancialInstrumentException;
	
	//Loans
	public BigDecimal getLoansMaxAmount() {
		return applicationProperties.getLoanMaxAmount();
	};
	public TermUnit getDefaultLoansMaxTermUnit() {
		return applicationProperties.getLoanMaxTermUnit();
	};
	public Long getLoansMaxTerm() {	
		return applicationProperties.getLoanMaxTerm();	
	};
	public BigDecimal getLoansCostMultiplier() {	
		return applicationProperties.getLoanCostMultipier();	
	};
	public String getLoansMinRejectTime() {
		return applicationProperties.getLoanMinRejectTime();
	}
	public String getLoansMaxRejectTime() {
		return applicationProperties.getLoanMaxRejectTime();
	}

}
