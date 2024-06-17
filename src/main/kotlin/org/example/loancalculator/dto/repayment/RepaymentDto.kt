package org.example.loancalculator.dto.repayment

import java.time.LocalDate

data class RepaymentDto(
    val loanAccount: String,
    val repaymentAmount: Int,
    val repaymentDate: LocalDate,
    val principalRepaid: Int,
    val interestRepaid: Int,
    val currentInterestRate: Double
)
