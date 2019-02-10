package com.loanManager.rest.domain;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
/**
 * Installment entity class
 * 
 * @author Rados³aw Ba³trukiewicz
 * 
 */
@Entity
public class Installment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY )
	@JsonProperty(access = Access.READ_ONLY)
	@Column(name = "installment_id")
	private Long id;
	@NotNull
 	private BigDecimal capitalRate;
	@NotNull
	private BigDecimal interestRate;
	@NotNull
	private Date dueDate;
	@NotNull
	private boolean paid;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public BigDecimal getCapitalRate() {
		return capitalRate;
	}
	public void setCapitalRate(BigDecimal capitalRate) {
		this.capitalRate = capitalRate;
	}
	public BigDecimal getInterestRate() {
		return interestRate;
	}
	public void setInterestRate(BigDecimal interestRate) {
		this.interestRate = interestRate;
	}
	public Date getDueDate() {
		return dueDate;
	}
	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}
	public boolean isPaid() {
		return paid;
	}
	public void setPaid(boolean paid) {
		this.paid = paid;
	}
	@Override
	public String toString() {
		return "Installment [id=" + id + ", capitalRate=" + capitalRate + ", interestRate=" + interestRate
				+ ", dueDate=" + dueDate + ", paid=" + paid + "]";
	}
	public Installment(@NotNull BigDecimal capitalRate, @NotNull BigDecimal interestRate, @NotNull Date dueDate,
			@NotNull boolean paid) {
		super();
		this.capitalRate = capitalRate;
		this.interestRate = interestRate;
		this.dueDate = dueDate;
		this.paid = paid;
	}
public Installment() {
}
	
}
