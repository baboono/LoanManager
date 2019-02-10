package com.loanManager.rest.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.loanManager.enums.TermUnit;



public interface FinancialInstrument {

	void setTermUnit(TermUnit termUnit);

	TermUnit getTermUnit();

	void setDueDate(Date dueDate);

	Date getDueDate();

	void setCost(BigDecimal cost);

	BigDecimal getCost();

	void setAmount(BigDecimal amount);

	BigDecimal getAmount();

	void setTerm(Long term);

	Long getTerm();

	Long getId();

}
