package org.example.loancalculator.response

import java.time.LocalDate

data class LoanDetailsResponse(
    val principalBalance: Int,
    val nextRepayment: Int,
    val nextRepaymentDate: LocalDate
)
