package org.example.loancalculator.dao

import org.example.loancalculator.entity.LoanInfo
import org.example.loancalculator.entity.RepaymentRecord
import org.springframework.data.jpa.repository.JpaRepository

interface RepaymentRecordDao : JpaRepository<RepaymentRecord, Long> {
    // 根據貸款資訊找到還款紀錄
    fun findByLoanInfo(loanInfo: LoanInfo): List<RepaymentRecord>
}