package org.example.loancalculator.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class EarlyPrincipalRepaymentDto(
    @field:NotBlank(message = "貸款帳號不能為空")
    val loanAccount: String,

    @field:NotNull(message = "提前還本金不能為空")
    val earlyPrincipalRepayment: Int
)
