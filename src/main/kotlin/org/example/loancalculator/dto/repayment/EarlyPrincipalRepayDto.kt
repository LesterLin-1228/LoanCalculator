package org.example.loancalculator.dto.repayment

data class EarlyPrincipalRepayDto(
    val principalBalance: Int,
    val nextRepaymentAmount: Int,
)
