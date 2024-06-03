package org.example.loancalculator.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import java.time.LocalDate

@Entity
data class LoanRate(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne
    @JoinColumn(name = "loan_account")
    val loanInfo: LoanInfo = LoanInfo(),

    val rateStartDate: LocalDate = LocalDate.now(),

    val rateDifference: Double = 0.0
)
