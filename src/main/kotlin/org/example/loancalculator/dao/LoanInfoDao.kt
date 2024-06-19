package org.example.loancalculator.dao

import org.example.loancalculator.entity.LoanInfo
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface LoanInfoDao : JpaRepository<LoanInfo, String> {
    // 根據貸款帳號找出貸款資訊
    fun findByLoanAccount(loanAccount: String): LoanInfo?

    // 取得總放款金額
    @Query("select sum(loanAmount) from LoanInfo")
    fun getTotalLoanAmount(): Int

    // 取得總還款金額
    @Query("select sum(totalAmountRepayment) from LoanInfo")
    fun getTotalRepaidAmount(): Int

    // 取得總利息收入
    @Query("SELECT SUM(totalInterestRepayment) FROM LoanInfo")
    fun getTotalInterestReceived(): Int
}