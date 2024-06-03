package org.example.loancalculator.model

data class Payment(
    val period: Int, // 當期期數
    val principalForPeriod: Int, // 當期還本金額
    val interestForPeriod: Int, // 當期利息金額
    val monthlyPayment: Int, // 月付本息金額
    val remainingPrincipal: Int, // 本金餘額
    val totalInterestAccrued: Int // 累計利息
)