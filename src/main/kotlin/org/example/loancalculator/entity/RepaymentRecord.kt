package org.example.loancalculator.entity

import jakarta.persistence.*
import org.hibernate.annotations.Comment

@Entity
class RepaymentRecord(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    val loanAccount: String,

    @Comment("還款金額")
    @Column(nullable = false)
    val repayment: Int,

    @Comment("還款本金")
    @Column(nullable = false)
    val principalRepaid: Int,

    @Comment("還款利息")
    @Column(nullable = false)
    val interestRepaid: Int,

    @Comment("當下利率(基礎利率+利率差)")
    @Column(nullable = false)
    val currentRate: Double,
)
