package org.example.loancalculator.dao

import org.example.loancalculator.entity.LoanInfo
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface LoanInfoDao : JpaRepository<LoanInfo, String> {
    // 根據貸款帳號找出貸款資訊
    fun findByLoanAccount(loanAccount: String): LoanInfo?

    // 取得總放款金額
    @Query("select sum(l.loanAmount) from LoanInfo l")
    fun getTotalLoanAmount(): Int

    // 取得總還款金額
    @Query("select sum(l.totalAmountRepayment) from LoanInfo l")
    fun getTotalRepaidAmount(): Int

    // 取得總利息收入
    @Query("SELECT SUM(l.totalInterestRepayment) FROM LoanInfo l")
    fun getTotalInterestReceived(): Int
}