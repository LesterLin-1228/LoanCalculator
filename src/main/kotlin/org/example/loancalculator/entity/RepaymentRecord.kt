package org.example.loancalculator.entity

import jakarta.persistence.*
import java.time.LocalDate

@Entity
data class RepaymentRecord(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne
    @JoinColumn(name = "loan_account")
    val loanInfo: LoanInfo = LoanInfo(),

    val repaymentDate: LocalDate = LocalDate.now(),

    val repaymentAmount: Double = 0.0,

    val repaymentPrincipal: Double = 0.0,

    val repaymentInterest: Double = 0.0,

    val currentRate: Double = 0.0,
)
