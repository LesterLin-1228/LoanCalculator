package org.example.loancalculator.dto.loanInfo

import java.time.LocalDate

data class LoanInfoDto(
    val loanAccount: String,
    val loanAmount: Int,
    val loanTerm: Int,
    val rateDifference: Double = 0.5,
    val startDate:LocalDate,
    val endDate:LocalDate,
    val repaymentDueDay:Int,
)
