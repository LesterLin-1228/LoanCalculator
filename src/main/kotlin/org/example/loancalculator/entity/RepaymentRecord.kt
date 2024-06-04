package org.example.loancalculator.entity

import jakarta.persistence.*
import org.jetbrains.annotations.NotNull

@Entity
data class RepaymentRecord(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne
    @JoinColumn(name = "loan_account")
    val loan: Loan? = null,

    @field:NotNull
    @Column(nullable = false)
    val repayment: Double = 0.0,

    @field:NotNull
    @Column(nullable = false)
    val principalRepaid: Double = 0.0,

    @field:NotNull
    @Column(nullable = false)
    val interestRepaid: Double = 0.0,

    @field:NotNull
    @Column(nullable = false)
    val currentRate: Double = 0.0,
)