package org.example.loancalculator.model

data class LoanRequest(
    val loanAmount: Double, // 貸款金額(單位萬元)
    val loanPeriod: Int, // 貸款期間(單位月)
    val isSingleRate: Boolean, // 是否為單一利率
    val interestRate: Double?, // 單一利率
    val ratePeriods: List<RatePeriod>?, // N段式利率
    val gracePeriod: Int, // 寬限期(單位月)
    val relatedFees: Double // 新增的相關費用
)