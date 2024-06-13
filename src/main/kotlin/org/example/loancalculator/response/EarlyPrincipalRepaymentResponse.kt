package org.example.loancalculator.response

import java.time.LocalDate

data class EarlyPrincipalRepaymentResponse(
    val principalBalance: Int,
    val nextRepaymentAmount: Int,
    val nextRepaymentDate: LocalDate
)
