package org.example.loancalculator.model

data class LoanResponse(
    val loanAmount: Int, // 貸款金額(單位萬元)
    val totalApr: Double, // 總費用年百分率
    val payments: List<Payment> // 每期還款資訊
)