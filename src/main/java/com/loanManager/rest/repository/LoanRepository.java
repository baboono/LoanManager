package com.loanManager.rest.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.loanManager.rest.domain.Loan;
/**
 * Default spring repository
 * 
 * @author Radosław Bałtrukiewicz
 * 
 */
public interface LoanRepository extends JpaRepository<Loan, Long> {

}