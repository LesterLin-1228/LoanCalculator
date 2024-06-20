package org.example.loancalculator.service

import org.example.loancalculator.dao.LoanInterestRateDao
import org.example.loancalculator.entity.LoanInfo
import org.example.loancalculator.model.LoanRequest
import org.example.loancalculator.model.LoanResponse
import org.example.loancalculator.model.Payment
import org.example.loancalculator.service.impl.InterestRateServiceImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import kotlin.math.abs
import kotlin.math.pow

@Service
class LoanCalculatorService(
    @Autowired private val interestRateServiceImpl: InterestRateServiceImpl,
    @Autowired private val loanInterestRateDao: LoanInterestRateDao
) {

    // 計算貸款詳情
    fun calculateLoan(request: LoanRequest): LoanResponse {
        // 將貸款金額轉換為元
        val loanAmountInDollars = request.loanAmount * 10000
        // 總期數
        val totalPeriod = request.loanPeriod
        // 寬限期
        val gracePeriod = request.gracePeriod
        // 相關費用
        val relatedFees = request.relatedFees
        // 初始化本金餘額
        var remainingPrincipal = loanAmountInDollars
        // 初始化累計利息
        var totalInterestAccrued = 0
        // 初始化每期付款列表
        val payments = mutableListOf<Payment>()

        if (request.isSingleRate) {
            // 單一利率計算
            val monthlyRate = request.interestRate!! / 100 / 12
            // 計算每月還款金額（包括本金和利息）
            var monthlyPayment = calculateMonthlyPayment(remainingPrincipal, monthlyRate, totalPeriod - gracePeriod)
            // 遍歷所有期數
            for (period in 1..totalPeriod) {
                // 計算當期利息
                val interestForPeriod = roundToInteger(remainingPrincipal * monthlyRate)
                // 計算當期的本金
                var principalForPeriod: Int

                // 在寬限期內，本期本金為0
                if (period <= gracePeriod) {
                    principalForPeriod = 0
                } else {
                    // 計算本期應還本金
                    principalForPeriod = monthlyPayment - interestForPeriod
                    // 更新剩餘本金
                    remainingPrincipal -= principalForPeriod
                }
                // 更新累計利息
                totalInterestAccrued += interestForPeriod

                // 最後一期，確保所有餘額和利息都結算
                if (period == totalPeriod) {
                    principalForPeriod += remainingPrincipal
                    monthlyPayment += remainingPrincipal
                    remainingPrincipal = 0
                }

                // 添加每期還款資訊
                payments.add(
                    Payment(
                        period = period,
                        principalForPeriod = principalForPeriod,
                        interestForPeriod = interestForPeriod,
                        monthlyPayment = if (period <= gracePeriod) interestForPeriod else monthlyPayment,
                        principalBalance = remainingPrincipal,
                        totalInterestAccrued = totalInterestAccrued
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
            var monthlyPayment = calculateMonthlyPayment(remainingPrincipal, monthlyRate, totalPeriod - gracePeriod)

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

                val interestForPeriod = roundToInteger(remainingPrincipal * monthlyRate)
                var principalForPeriod: Int

                if (period <= gracePeriod) {
                    principalForPeriod = 0
                    monthlyPayment = interestForPeriod
                } else {
                    // 如果當前期數為寬限期後的第一期或當前利率段已經到期並切換到第二段利率，就重新計算 monthlyPayment
                    if (period == gracePeriod + 1 || remainingMonthsInSegment == ratePeriods[currentRateIndex].months) {
                        monthlyPayment =
                            calculateMonthlyPayment(remainingPrincipal, monthlyRate, totalPeriod - period + 1)
                    }
                    principalForPeriod = monthlyPayment - interestForPeriod
                    remainingPrincipal -= principalForPeriod
                }
                totalInterestAccrued += interestForPeriod

                if (period == totalPeriod) {
                    principalForPeriod += remainingPrincipal
                    monthlyPayment += remainingPrincipal
                    remainingPrincipal = 0
                }

                payments.add(
                    Payment(
                        period = period,
                        principalForPeriod = principalForPeriod,
                        interestForPeriod = interestForPeriod,
                        monthlyPayment = monthlyPayment,
                        principalBalance = remainingPrincipal,
                        totalInterestAccrued = totalInterestAccrued
                    )
                )
                // 減少當前利率段剩餘的月份數
                remainingMonthsInSegment--
            }
        }

        // 計算總費用年百分率
        val totalApr = calculateApr(loanAmountInDollars, relatedFees, payments)

        // 返回最終的貸款回應
        return LoanResponse(
            loanAmount = request.loanAmount,
            totalApr = totalApr,
            payments = payments
        )
    }

    // 計算每月還款金額
    fun calculateMonthlyPayment(
        loanAmount: Int,
        monthlyRate: Double,
        periods: Int
    ): Int {
        // 使用等額本息公式計算每月還款金額
        return roundToInteger(loanAmount * monthlyRate / (1 - (1 + monthlyRate).pow(-periods)))
    }

    // 使用牛頓法計算總費用年百分率
    private fun calculateApr(
        loanAmount: Int,
        relatedFees: Int,
        payments: List<Payment>,
    ): Double {
        var apr = 0.1 // 初始估計的 APR 設定為10%
        val epsilon = 1e-6 // 誤差容許範圍
        var iteration = 0 // 迭代次數
        val maxIterations = 1000 // 最大迭代次數限制

        // 將 relatedFees 轉換為 Double
        val fees = relatedFees.toDouble()

        while (iteration < maxIterations) {
            var f = fees
            var fPrime = 0.0 // 初始函數導數值

            // 計算函數值和函數導數值
            for (payment in payments) {
                // 對每期的付款計算折現後的付款金額，並更新 f 和 fPrime
                val discountedPayment = payment.monthlyPayment / (1 + apr / 12).pow(payment.period)
                f += discountedPayment
                fPrime -= payment.period * discountedPayment / (1 + apr / 12)
            }

            // 如果函數值與貸款金額的差距小於誤差範圍，則返回計算結果
            if (abs(f - loanAmount) < epsilon) {
                // 用 BigDecimal 來處理小數的進位，避免誤差
                return BigDecimal(apr * 100).setScale(2, RoundingMode.HALF_UP).toDouble() // 四捨五入為百分比並取到小數點第二位
            }

            // 使用牛頓法計算下一次迭代的APR值
            apr -= (f - loanAmount) / fPrime
            iteration++
        }

        return BigDecimal(apr * 100).setScale(2, RoundingMode.HALF_UP).toDouble()
    }

    fun roundToInteger(value: Double): Int {
        return BigDecimal(value).setScale(0, RoundingMode.HALF_UP).toInt()
    }

    fun prepareLoanRequest(loanInfo: LoanInfo): LoanResponse {

        val currentInterestRate = getCurrentInterestRate(loanInfo.loanAccount)

        val loanRequest = LoanRequest(
            loanAmount = loanInfo.loanAmount / 10000,
            loanPeriod = loanInfo.loanTerm,
            isSingleRate = true,
            interestRate = currentInterestRate,
            gracePeriod = 0,
            ratePeriods = null,
            relatedFees = 0
        )
        // 傳回計算後的貸款資訊
        return calculateLoan(loanRequest)
    }

    // 計算下次還款日
    fun calculateNextRepaymentDate(
        startDate: LocalDate,
        lastRepaymentDate: LocalDate?,
        repaymentDueDay: Int
    ): LocalDate {
        val nextRepaymentDate = if (lastRepaymentDate != null) {
            lastRepaymentDate.plusMonths(1)
        }else {
            startDate.plusMonths(1)
        }
        return LocalDate.of(nextRepaymentDate.year, nextRepaymentDate.month, repaymentDueDay)
    }

    // 根據貸款帳號計算當前利率
    fun getCurrentInterestRate(loanAccount: String): Double {
        // 獲取最新的基礎利率
        val baseRate = interestRateServiceImpl.getLatestInterestRate().baseRate
        // 獲取利率差
        val rateDifference =
            loanInterestRateDao.findByLoanAccount(loanAccount)?.rateDifference ?: throw Exception("查無利率差")
        return baseRate + rateDifference
    }
}