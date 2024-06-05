package org.example.loancalculator.entity

import jakarta.persistence.*
import org.hibernate.annotations.Comment
import java.time.LocalDate

@Entity
class Loan(
    @Id
    val account: String,

    @Column(nullable = false)
    val startDate: LocalDate,

    @Column(nullable = false)
    val endDate: LocalDate,

    @Comment("每月還款日")
    @Column(nullable = false)
    val paymentDueDay: Int,

    @Column(nullable = false)
    val remainingPrincipal: Double,

    @Comment("已繳總金額(已繳本金+已繳總利息)")
    @Column(nullable = false)
    val totalRepayment: Double,

    @Comment("已繳本金")
    @Column(nullable = false)
    val totalPrincipalRepaid: Double,

    @Comment("已繳總利息")
    @Column(nullable = false)
    val totalInterestRepaid: Double,

    @Column(nullable = false)
    val loanAmount: Double,

    @Column(nullable = false)
    val loanTerm: Int,
)
