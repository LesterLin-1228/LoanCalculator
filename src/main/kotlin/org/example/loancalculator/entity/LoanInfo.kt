package org.example.loancalculator.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id
import org.jetbrains.annotations.NotNull
import java.time.LocalDate

@Entity
data class LoanInfo(
    @Id
    val loanAccount: String = "",

    @NotNull
    val startDate: LocalDate = LocalDate.now(),

    @NotNull
    val endDate: LocalDate = LocalDate.now(),

    @NotNull
    val monthlyRepaymentDate: Int = 0,

    @NotNull
    val remainingPrincipal: Double = 0.0,

    val totalPaidAmount: Double = 0.0,

    val totalPaidPrincipal: Double = 0.0,

    val totalPaidInterest: Double = 0.0,

    @NotNull
    val loanAmount: Double = 0.0,

    @NotNull
    val loanPeriod: Int = 0,
)
