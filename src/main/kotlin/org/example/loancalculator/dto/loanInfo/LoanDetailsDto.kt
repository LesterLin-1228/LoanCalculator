package org.example.loancalculator.dto.loanInfo

import java.time.LocalDate

data class LoanDetailsDto(
    val principalBalance: Int,
    val nextRepayment: Int,
    val nextRepaymentDate: LocalDate
)
