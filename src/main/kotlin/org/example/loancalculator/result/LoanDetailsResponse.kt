package org.example.loancalculator.result

data class LoanDetailsResponse(
    val remainingPrincipal: Int,
    val nextRepayment: Int
)
