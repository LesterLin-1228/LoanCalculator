package org.example.loancalculator.dto.repayment

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive
import java.time.LocalDate

data class RepaymentReq(
    @field:NotBlank(message = "帳號不能為空格或空值")
    val loanAccount: String,

    val repaymentDate: LocalDate = LocalDate.now(),

    @field:Positive(message = "還款金額須為正數")
    val repaymentAmount: Int
)
