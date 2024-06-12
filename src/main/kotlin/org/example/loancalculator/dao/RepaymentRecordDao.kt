package org.example.loancalculator.dao

import org.example.loancalculator.entity.RepaymentRecord
import org.springframework.data.jpa.repository.JpaRepository

interface RepaymentRecordDao : JpaRepository<RepaymentRecord, Long> {
    fun findByLoanAccount(loanAccount: String): List<RepaymentRecord>
}