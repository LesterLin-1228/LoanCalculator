package org.example.loancalculator.dto.loanInfo

data class LoanStatisticsDto(
    val totalLoanAmount: Int,
    val totalRepaidAmount: Int,
    val totalInterestReceived: Int
)
