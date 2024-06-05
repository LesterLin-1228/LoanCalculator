package org.example.loancalculator.dao

import org.example.loancalculator.entity.Loan
import org.springframework.data.jpa.repository.JpaRepository

interface LoanDao : JpaRepository<Loan, String> {
    fun findByLoanAccount(loanAccount: String): Loan?
}