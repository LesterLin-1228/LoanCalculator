package org.example.loancalculator.response

data class LoanDetailsResponse(
    val principalBalance: Int,
    val nextRepayment: Int
)
