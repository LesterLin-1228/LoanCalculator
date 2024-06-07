package org.example.loancalculator.entity

import jakarta.persistence.*
import org.hibernate.annotations.Comment
import java.time.LocalDate

@Entity
class LoanInfo(
    @Id
    val loanAccount: String,

    @Column(nullable = false)
    val startDate: LocalDate,

    @Column(nullable = false)
    var endDate: LocalDate,

    @Comment("每月還款日")
    @Column(nullable = false)
    val paymentDueDay: Int,

    @Comment("剩餘本金(起始為貸款金額)")
    @Column(nullable = false)
    var remainingPrincipal: Int,

    @Comment("已繳總金額(已繳本金+已繳總利息)")
    @Column(nullable = false)
    val totalRepayment: Int = 0,

    @Comment("已繳本金")
    @Column(nullable = false)
    val totalPrincipalRepaid: Int = 0,

    @Comment("已繳總利息")
    @Column(nullable = false)
    val totalInterestRepaid: Int = 0,

    @Comment("貸款總額(單位:元)")
    @Column(nullable = false)
    val loanAmount: Int,

    @Comment("貸款期數(單位:月)")
    @Column(nullable = false)
    val loanTerm: Int,
)

