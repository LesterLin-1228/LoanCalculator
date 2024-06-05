package org.example.loancalculator.entity

import jakarta.persistence.*
import org.hibernate.annotations.Comment
import java.time.LocalDate

@Entity
class RepaymentRecord(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,

    @ManyToOne
    @JoinColumn(name = "loan_account")
    val loan: Loan,

    @Comment("還款金額")
    @Column(nullable = false)
    val repayment: Double,

    @Comment("還款本金")
    @Column(nullable = false)
    val principalRepaid: Double,

    @Comment("還款利息")
    @Column(nullable = false)
    val interestRepaid: Double,

    @Comment("當下利率")
    @Column(nullable = false)
    val currentRate: Double,

    @Comment("還款日期")
    @Column(nullable = false)
    val repaymentDate: LocalDate
)
