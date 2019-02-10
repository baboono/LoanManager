package com.loanManager.rest.endpoint;

import java.math.BigDecimal;
import java.time.LocalTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.loanManager.rest.config.ApplicationProperties;
import com.loanManager.rest.domain.Loan;

/**Loan application is rejected if: 
 * Rule no 1. validation rejects null or 0 term/amount values.
 * Rule no 2. if application is not within amount/term range.
 * Rule no 3. if application is between 00:00 and 06:00 and max amount is asked.
 * application. See application.properties
 * @author Rados³aw Ba³trukiewicz
 * @throws NullPointerException
 */
@Component
public class LoanValidator implements Validator {

	public static final String LOAN_REJECTED = "Loan rejected";
	@Autowired
	private ApplicationProperties applicationProperties;

	@Override
	public boolean supports(Class<?> clazz) {
		return Loan.class.isAssignableFrom(clazz);
	}
	

	@Override
	public void validate(Object target, Errors errors) {
		Loan loan = null;
		try {
			loan = (Loan) target;
		} catch (ClassCastException e) {
			errors.rejectValue("Validating target instrument", "Must be type of Loan Financial Instrument");
		}
		BigDecimal amount = loan.getAmount();
		Long term = loan.getTerm();
		// Rule nr 1
		if (amount == null) {
			throw new NullPointerException("Loan amount cannot be null."+LOAN_REJECTED);
		}
		if (term == null) {
			throw new NullPointerException("Loan term cannot be null."+LOAN_REJECTED);
		}
		if (amount.equals(BigDecimal.ZERO)) {
			errors.rejectValue("amount", "", "Loan amount cannot be zero."+LOAN_REJECTED);
		}
		if (term == 0) {
			errors.rejectValue("term", "", "Loan term cannot be zero."+LOAN_REJECTED);
		}
		if (amount.compareTo(BigDecimal.ZERO) == -1) {
			errors.rejectValue("amount", "", "Loan amount cannot be negative."+LOAN_REJECTED);
		}
		if (term < 0) {
			errors.rejectValue("term", "", "Loan term cannot be negative."+LOAN_REJECTED);
		}
		// Rule nr 2
		if (applicationProperties.getLoanMaxTerm().compareTo(loan.getTerm()) == -1) {
			errors.rejectValue("term", "", "Loan term extends max term."+LOAN_REJECTED);
		}
		if (applicationProperties.getLoanMaxAmount().compareTo(loan.getAmount()) == -1) {
			errors.rejectValue("amount", "", "Loan amount extends max amount."+LOAN_REJECTED);
		}
		// Rule no 3
		LocalTime zonedTime = LocalTime.now();
		Boolean targetInZone = (zonedTime.isAfter(LocalTime.parse(applicationProperties.getLoanMinRejectTime()))
				&& zonedTime.isBefore(LocalTime.parse(applicationProperties.getLoanMaxRejectTime())));
		if (targetInZone && applicationProperties.getLoanMaxAmount().compareTo(loan.getAmount()) == 0) {
			errors.rejectValue("amount", "",
					"Server time is between :" + applicationProperties.getLoanMinRejectTime() + " and "
							+ applicationProperties.getLoanMaxRejectTime() + " and Max Loan ammount = "
							+ applicationProperties.getLoanMaxAmount() + " was asked."+LOAN_REJECTED);
		}
	}

}