package com.loanManager.rest.config;



import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import com.loanManager.enums.TermUnit;





/**
 * Configuration. Taken from application.properties file
 * 
 * @author Rados³aw Ba³trukiewicz
 * 
 */
@Component
@PropertySource("classpath:application.properties")
public class ApplicationProperties {
	//LOANS
	@Value("${loanManagerDemo.config.loan.time.reject.max}")
    private String loanMaxRejectTime;
	
	@Value("${loanManagerDemo.config.loan.time.reject.min}")
    private String loanMinRejectTime;
	
    @Value("${loanManagerDemo.config.loan.maxAmount}")
    private BigDecimal loanMaxAmount;

    @Value("${loanManagerDemo.config.loan.maxTerm}")
    private Long loanMaxTerm;
    
    @Value("${loanManagerDemo.config.loan.maxTermUnit.default}")
    private TermUnit loanMaxTermUnit;
    
    @Value("${loanManagerDemo.config.loan.cost}")
    private BigDecimal loanCostMultipier;

	public BigDecimal getLoanCostMultipier() {
		return loanCostMultipier;
	}

	public BigDecimal getLoanMaxAmount() {
		return loanMaxAmount;
	}

	public TermUnit getLoanMaxTermUnit() {
		return loanMaxTermUnit;
	}

	public Long getLoanMaxTerm() {
		return loanMaxTerm;
	}

	public String getLoanMaxRejectTime() {
		return loanMaxRejectTime;
	}

	public String getLoanMinRejectTime() {
		return loanMinRejectTime;
	}

	public void setLoanMaxRejectTime(String loanMaxRejectTime) {
		this.loanMaxRejectTime = loanMaxRejectTime;
	}

	public void setLoanMinRejectTime(String loanMinRejectTime) {
		this.loanMinRejectTime = loanMinRejectTime;
	}


}
