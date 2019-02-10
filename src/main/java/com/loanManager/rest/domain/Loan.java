package com.loanManager.rest.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.loanManager.enums.TermUnit;
/**
 * Loan entity class
 * 
 * @author Rados³aw Ba³trukiewicz
 * 
 */
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Loan implements FinancialInstrument {

    @Id
    @JsonProperty(access = Access.READ_ONLY)
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="loan_id")
    private Long id;

    @NotNull
    @Column(name="term", nullable = false)
    private Long term;

    @NotNull
    @Column(name="amount", nullable = false)
    private BigDecimal amount;

    @Column(name="cost")
    @JsonProperty(access = Access.READ_ONLY)
    private BigDecimal cost;

    @Column(name="dueDate")
    @JsonProperty(access = Access.READ_ONLY)
    private Date dueDate;
    
    @Column(name="termUnit")
    @JsonProperty(access = Access.READ_ONLY)
    private TermUnit termUnit;
    
	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "loan_id")
	private Set<Installment> installments;
   
    public Loan(Long term, BigDecimal amount) {
    	this.term = term;
    	this.amount = amount;
    }
    public Loan() {
        }
    @Override
    public Long getId() {
		return id;
	}
    @Override
	public Long getTerm() {
		return term;
	}
    @Override
	public void setTerm(Long term) {
		this.term = term;
	}
    @Override
	public BigDecimal getAmount() {
		return amount;
	}
    @Override
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
    @Override
	public BigDecimal getCost() {
		return cost;
	}
    @Override
	public void setCost(BigDecimal cost) {
		this.cost = cost;
	}
    @Override
	public Date getDueDate() {
		return dueDate;
	}
    @Override
	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}
    @Override
	public TermUnit getTermUnit() {
		return termUnit;
	}
    @Override
	public void setTermUnit(TermUnit termUnit) {
		this.termUnit = termUnit;
	}
	
	public Set<Installment> getInstallments() {
		return installments;
	}

	public void setInstallments(Set<Installment> addresses) {
		this.installments = addresses;
	}
	
	public void addInstallments(Installment address) {
		
		if (getInstallments() == null) {
			setInstallments(new HashSet<>());
		}
		getInstallments().add(address);
	}

@Override
public int hashCode() {
	// TODO Auto-generated method stub
	return super.hashCode();
}
/**
 * Simplyfied version of equals for comparing loans.
 * 
 */
@Override
public boolean equals(Object obj) {
	if(!obj.getClass().equals(Loan.class)) return false;
	Loan loan = (Loan) obj;
	if(getId() != null && loan.getId() != null) return getId().equals(loan.getId());
	else {
	if(getAmount().equals(loan.getAmount()) && getTerm()==loan.getTerm()){return true;}
	}
	return false;
	
}

@Override
public String toString() {
	return "Loan [id=" + id + ", term=" + term + ", amount=" + amount + ", cost=" + cost + ", dueDate=" + dueDate
			+ ", termUnit=" + termUnit + ", installments=" + installments + "]";
}

}