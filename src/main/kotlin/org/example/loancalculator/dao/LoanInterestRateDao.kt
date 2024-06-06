package org.example.loancalculator.dao

import org.example.loancalculator.entity.LoanInterestRate
import org.springframework.data.jpa.repository.JpaRepository

interface LoanInterestRateDao : JpaRepository<LoanInterestRate, Long> {
    fun findByLoanAccount(loanAccount: String): LoanInterestRate?
}