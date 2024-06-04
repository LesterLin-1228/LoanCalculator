package org.example.loancalculator.entity

import jakarta.persistence.*
import org.jetbrains.annotations.NotNull
import java.time.LocalDate

@Entity
data class Loan(
    @Id
    val account: String = "",

    @field:NotNull
    @Column(nullable = false)
    val startDate: LocalDate = LocalDate.now(),

    @field:NotNull
    @Column(nullable = false)
    val endDate: LocalDate = LocalDate.now(),

    @field:NotNull
    @Column(nullable = false)
    val paymentDueDay: Int = 0,

    @field:NotNull
    @Column(nullable = false)
    val remainingPrincipal: Double = 0.0,

    val totalRepayment: Double = 0.0,

    val totalPrincipalRepaid: Double = 0.0,

    val totalInterestRepaid: Double = 0.0,

    @field:NotNull
    @Column(nullable = false)
    val principalAmount: Double = 0.0,

    @field:NotNull
    @Column(nullable = false)
    val termMonths: Int = 0,

    @OneToMany(mappedBy = "loan",cascade = [CascadeType.ALL], orphanRemoval = true)
    val interestRates: List<LoanInterestRate> = mutableListOf(),

    @OneToMany(mappedBy = "loan",cascade = [CascadeType.ALL], orphanRemoval = true)
    val repaymentHistory: List<RepaymentRecord> = mutableListOf()
)
