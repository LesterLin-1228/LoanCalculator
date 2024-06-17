package org.example.loancalculator.dao

import org.example.loancalculator.entity.LoanInfo
import org.example.loancalculator.entity.RepaymentRecord
import org.springframework.data.jpa.repository.JpaRepository

interface RepaymentRecordDao : JpaRepository<RepaymentRecord, Long> {
    fun findByLoanInfo(loanInfo: LoanInfo): List<RepaymentRecord>
}