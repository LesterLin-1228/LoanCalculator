package org.example.loancalculator.dto.loanInfo

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import java.time.LocalDate

data class LoanInfoReq(
    @field:NotBlank(message = "帳號不能為空格或空值")
    val loanAccount: String,

    @field:Positive(message = "貸款金額必須為正數")
    val loanAmount: Int,

    @field:Positive(message = "貸款期數必須為正數")
    val loanTerm: Int,

    @field:NotNull(message = "起始日不能為空值")
    val startDate: LocalDate = LocalDate.now(),

    @field:NotNull(message = "結束日不能為空值")
    val endDate: LocalDate = calculateEndDate(startDate, loanTerm),

    @field:NotNull(message = "每月還款日不能為空值")
    val repaymentDueDay: Int = startDate.dayOfMonth,

    @field:NotNull(message = "本金餘額不能為空值")
    val principalBalance: Int = loanAmount,

    @field:NotNull(message = "已繳總金額不能為空值")
    val totalAmountRepayment: Int = 0,

    @field:NotNull(message = "已繳本金不能為空值")
    val totalPrincipalRepayment: Int = 0,

    @field:NotNull(message = "已繳利息不能為空值")
    val totalInterestRepayment: Int = 0,

    @field:Positive(message = "利率差必須為正數")
    val rateDifference: Double,
) {
    companion object {
        // 定義一個輔助函數計算 endDate
        private fun calculateEndDate(startDate: LocalDate, loanTerm: Int): LocalDate {
            return startDate.plusMonths(loanTerm.toLong())
        }
    }
}
