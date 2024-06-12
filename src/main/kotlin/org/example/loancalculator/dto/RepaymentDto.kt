package org.example.loancalculator.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.LocalDate

data class RepaymentDto(
    @field:NotBlank(message = "帳號不能為空格或空值")
    val loanAccount: String,

    val repaymentDate: LocalDate? = null,

    @field:NotNull(message = "還款金額不能為空值")
    val repaymentAmount: Int
)
