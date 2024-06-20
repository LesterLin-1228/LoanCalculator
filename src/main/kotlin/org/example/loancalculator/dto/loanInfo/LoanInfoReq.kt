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

    @field:Positive(message = "利率差必須為正數")
    val rateDifference: Double,
)