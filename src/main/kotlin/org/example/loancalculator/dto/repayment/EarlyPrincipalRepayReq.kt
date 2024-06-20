package org.example.loancalculator.dto.repayment

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive
import java.time.LocalDate

data class EarlyPrincipalRepayReq(
    @field:NotBlank(message = "貸款帳號不能為空")
    val loanAccount: String,

    @field:Positive(message = "提前還本金須為正數")
    val earlyPrincipalRepayment: Int,

    val earlyPrincipalRepaymentDate: LocalDate = LocalDate.now(),
)
