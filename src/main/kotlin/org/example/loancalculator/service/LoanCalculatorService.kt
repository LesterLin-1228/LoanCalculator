package org.example.loancalculator.service

import org.example.loancalculator.model.LoanRequest
import org.example.loancalculator.model.LoanResponse
import org.example.loancalculator.model.Payment
import org.springframework.stereotype.Service
import kotlin.math.pow
import kotlin.math.round

@Service
class LoanCalculatorService {

    fun calculateLoan(request: LoanRequest): LoanResponse {
        // 將貸款金額轉換為元
        val loanAmountInDollars = request.loanAmount * 10000
        // 總期數
        val totalPeriod = request.loanPeriod
        // 寬限期
        val gracePeriod = request.gracePeriod
        // 初始化本金餘額
        var remainingPrincipal = loanAmountInDollars
        // 初始化累計利息
        var totalInterestAccrued = 0.0
        // 初始化每期付款列表
        val payments = mutableListOf<Payment>()

        if (request.isSingleRate) {
            // 單一利率計算
            val monthlyRate = request.interestRate!! / 100 / 12
            val monthlyPayment = calculateMonthlyPayment(remainingPrincipal, monthlyRate, totalPeriod - gracePeriod)

            for (period in 1..totalPeriod) {
                val interestForPeriod = remainingPrincipal * monthlyRate
                val principalForPeriod: Double

                // 在寬限期內，本期本金為0
                if (period <= gracePeriod) {
                    principalForPeriod = 0.0
                } else {
                    // 計算本期應還本金
                    principalForPeriod = monthlyPayment - interestForPeriod
                    // 更新剩餘本金
                    remainingPrincipal -= principalForPeriod
                }
                // 累計利息
                totalInterestAccrued += interestForPeriod

                // 添加每期還款資訊
                payments.add(
                    Payment(
                        period = period,
                        principalForPeriod = round(principalForPeriod).toInt(),
                        interestForPeriod = round(interestForPeriod).toInt(),
                        monthlyPayment = if (period <= gracePeriod) round(interestForPeriod).toInt() else round(monthlyPayment).toInt(),
                        remainingPrincipal = round(remainingPrincipal).toInt(),
                        totalInterestAccrued = round(totalInterestAccrued).toInt()
                    )
                )
            }
        } else {
            // 多段式利率計算
            val ratePeriods = request.ratePeriods!!
            // 當前利率段的索引
            var currentRateIndex = 0
            // 當前利率段剩餘的月份數
            var remainingMonthsInSegment = ratePeriods[currentRateIndex].months
            // 當前利率段的月利率
            var monthlyRate = ratePeriods[currentRateIndex].rate / 100 / 12
            // 計算每月還款金額（包括本金和利息）
            var monthlyPayment = calculateMonthlyPayment(remainingPrincipal, monthlyRate, totalPeriod - gracePeriod)

            // 遍歷所有期數
            for (period in 1..totalPeriod) {

                // 檢查是否需要切換到下一個利率段
                if (currentRateIndex < ratePeriods.size - 1 && remainingMonthsInSegment == 0) {
                    // 切換到下一個利率段
                    currentRateIndex++
                    // 更新剩餘月份數為新利率段的月份數
                    remainingMonthsInSegment = ratePeriods[currentRateIndex].months
                    // 更新月利率為新利率段的月利率
                    monthlyRate = ratePeriods[currentRateIndex].rate / 100 / 12
                }
                // 計算當期利息
                val interestForPeriod = remainingPrincipal * monthlyRate
                // 計算當期的本金
                val principalForPeriod: Double
                // 更新剩餘本金
                if (period <= gracePeriod) {
                    principalForPeriod = 0.0
                    monthlyPayment = interestForPeriod
                } else {
                    // 如果當前期數為寬限期後的第一期或當前利率段已經到期並切換到第二段利率，就重新計算 monthlyPayment
                    if (period == gracePeriod + 1 || remainingMonthsInSegment == ratePeriods[currentRateIndex].months) {
                        monthlyPayment = calculateMonthlyPayment(remainingPrincipal, monthlyRate, totalPeriod - period + 1)
                    }
                    principalForPeriod = monthlyPayment - interestForPeriod
                    remainingPrincipal -= principalForPeriod
                }
                // 更新累計利息
                totalInterestAccrued += interestForPeriod

                // 添加每期還款資訊
                payments.add(
                    Payment(
                        period = period,
                        principalForPeriod = round(principalForPeriod).toInt(), // 四捨五入為整數
                        interestForPeriod = round(interestForPeriod).toInt(),
                        monthlyPayment = round(monthlyPayment).toInt(),
                        remainingPrincipal = round(remainingPrincipal).toInt(),
                        totalInterestAccrued = round(totalInterestAccrued).toInt()
                    )
                )
                // 減少當前利率段剩餘的月份數
                remainingMonthsInSegment--
            }
        }

        // 返回最終的貸款回應
        return LoanResponse(
            loanAmount = request.loanAmount,
            totalApr = 0.0,
            payments = payments
        )
    }

    // 計算每月還款金額
    private fun calculateMonthlyPayment(
        loanAmount: Double,
        monthlyRate: Double,
        periods: Int
    ): Double {
        // 使用等額本息公式計算每月還款金額
        return loanAmount * monthlyRate / (1 - (1 + monthlyRate).pow(-periods))
    }
}