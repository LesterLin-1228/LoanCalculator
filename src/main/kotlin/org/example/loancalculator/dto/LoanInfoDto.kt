package org.example.loancalculator.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import java.time.LocalDate

data class LoanInfoDto(
    @field:NotBlank(message = "帳號不能為空格或空值")
    val loanAccount: String,

    @field:NotNull(message = "貸款金額不能為空")
    @field:Positive(message = "貸款金額必須為正數")
    val loanAmount: Int,

    @field:NotNull(message = "貸款期限不能為空")
    @field:Positive(message = "貸款期限必須為正數")
    val loanTerm: Int,

    @field:NotNull
    val rateStartDate: LocalDate = LocalDate.now(),
    @field:NotNull
    @field:Positive(message = "利率差需為正數")
    val rateDifference: Double = 0.5
)
