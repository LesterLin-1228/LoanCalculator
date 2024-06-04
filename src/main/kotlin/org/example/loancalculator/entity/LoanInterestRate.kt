package org.example.loancalculator.entity

import jakarta.persistence.*
import org.jetbrains.annotations.NotNull
import java.time.LocalDate

@Entity
data class LoanInterestRate(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne
    @JoinColumn(name = "loan_account")
    val loan: Loan? = null,

    @field:NotNull
    @Column(nullable = false)
    val rateStartDate: LocalDate = LocalDate.now(),

    @field:NotNull
    @Column(nullable = false)
    val rateDifference: Double = 0.5
)
