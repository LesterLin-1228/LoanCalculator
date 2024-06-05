package org.example.loancalculator.entity

import jakarta.persistence.*
import org.hibernate.annotations.Comment
import java.time.LocalDate

@Entity
class LoanInterestRate(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,

    @ManyToOne
    @JoinColumn(name = "loan_account")
    val loan: Loan,

    @Comment("利率起始日")
    @Column(nullable = false)
    val rateStartDate: LocalDate,

    @Comment("利率差(最終利率-基礎利率)")
    @Column(nullable = false)
    val rateDifference: Double
)
