package com.loanManager.rest.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.loanManager.rest.domain.Loan;
/**
 * Default spring repository
 * 
 * @author Rados�aw Ba�trukiewicz
 * 
 */
public interface LoanRepository extends JpaRepository<Loan, Long> {

}