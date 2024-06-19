package org.example.loancalculator.entity

import jakarta.persistence.*
import org.hibernate.annotations.Comment
import java.time.LocalDate

@Entity
class LoanInfo(
    @Id
    val loanAccount: String,

    @Comment("貸款總額(單位:元)")
    @Column(nullable = false)
    val loanAmount: Int,

    @Comment("貸款期數(單位:月)")
    @Column(nullable = false)
    val loanTerm: Int,

    @Column(nullable = false)
    val startDate: LocalDate,

    @Column(nullable = false)
    val endDate: LocalDate,

    @Comment("每月還款日")
    @Column(nullable = false)
    val repaymentDueDay: Int,

    @Comment("本金餘額(起始為貸款金額)")
    @Column(nullable = false)
    var principalBalance: Int,

    @Comment("已繳總金額(已繳本金+已繳總利息)")
    @Column(nullable = false)
    var totalAmountRepayment: Int,

    @Comment("已繳總本金")
    @Column(nullable = false)
    var totalPrincipalRepayment: Int,

    @Comment("已繳總利息")
    @Column(nullable = false)
    var totalInterestRepayment: Int,
)

