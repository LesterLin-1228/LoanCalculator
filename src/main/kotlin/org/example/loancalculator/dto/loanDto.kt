package org.example.loancalculator.dto

import jakarta.validation.constraints.*
import java.time.LocalDate

data class loanDto(
    @field:NotBlank(message = "帳號不能為空格或空值")
    val account: String,

    @field:NotNull(message = "開始日期不能為空")
    val startDate: LocalDate,

    @field:NotNull(message = "結束日期不能為空")
    val endDate: LocalDate,

    @field:Min(value = 1, message = "每月還款日必須為正數")
    @field:Max(value = 31, message = "每月還款日不能超過31")
    val paymentDueDay: Int,

    @field:NotNull(message = "剩餘本金不能為空")
    @field:PositiveOrZero(message = "剩餘本金不能為負數")
    val remainingPrincipal: Double,

    @field:NotNull(message = "已繳總金額不能為空")
    @field:PositiveOrZero(message = "已繳總金額不能為負數")
    val totalRepayment: Double,

    @field:NotNull(message = "已繳本金不能為空")
    @field:PositiveOrZero(message = "已繳本金不能為負數")
    val totalPrincipalRepaid: Double,

    @field:NotNull(message = "已繳總利息不能為空")
    @field:PositiveOrZero(message = "已繳總利息不能為負數")
    val totalInterestRepaid: Double,

    @field:NotNull(message = "貸款金額不能為空")
    @field:Positive(message = "貸款金額必須為正數")
    val loanAmount: Double,

    @field:NotNull(message = "貸款期限不能為空")
    @field:Positive(message = "貸款期限必須為正數")
    val loanTerm: Int
)
