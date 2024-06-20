package org.example.loancalculator.dao

import org.example.loancalculator.entity.LoanInfo
import org.example.loancalculator.entity.RepaymentRecord
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.time.LocalDate

interface RepaymentRecordDao : JpaRepository<RepaymentRecord, Long> {
    // 根據貸款資訊找到還款紀錄
    fun findByLoanInfo(loanInfo: LoanInfo): List<RepaymentRecord>
    // 找到最近的還款日期
    @Query("select max(r.repaymentDate) from RepaymentRecord r where r.loanInfo.loanAccount = :loanAccount")
    fun findLatestRepaymentDateByLoanAccount(loanAccount:String): LocalDate?
}