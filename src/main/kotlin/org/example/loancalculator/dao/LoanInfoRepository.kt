package org.example.loancalculator.dao

import org.example.loancalculator.entity.LoanInfo
import org.springframework.data.jpa.repository.JpaRepository

interface LoanInfoRepository : JpaRepository<LoanInfo, String> {
    fun findByLoanAccount(loanAccount: String): LoanInfo?
}