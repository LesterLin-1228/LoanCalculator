package org.example.loancalculator.model

data class RatePeriod(
    val rate: Double, // 利率 (單位%)
    val months: Int // 利率適用的期間 (單位月)
)

data class LoanRequest(
    val loanAmount: Double, // 貸款金額(單位萬元)
    val loanPeriod: Int, // 貸款期間(單位月)
    val isSingleRate: Boolean, // 是否為單一利率
    val interestRate: Double?, // 單一利率
    val ratePeriods: List<RatePeriod>?, // N段式利率
    val gracePeriod: Int, // 寬限期(單位月)
    val relatedFees: Double // 新增的相關費用
)

data class Payment(
    val period: Int, // 當期期數
    val principalForPeriod: Int, // 當期還本金額
    val interestForPeriod: Int, // 當期利息金額
    val monthlyPayment: Int, // 月付本息金額
    val remainingPrincipal: Int, // 本金餘額
    val totalInterestAccrued: Int // 累計利息
)

data class LoanResponse(
    val loanAmount: Int, // 貸款金額(單位萬元)
    val totalApr: Double, // 總費用年百分率
    val payments: List<Payment> // 每期還款資訊
)
