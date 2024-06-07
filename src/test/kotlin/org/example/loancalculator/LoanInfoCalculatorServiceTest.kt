package org.example.loancalculator

import org.example.loancalculator.model.LoanRequest
import org.example.loancalculator.model.RatePeriod
import org.example.loancalculator.service.LoanCalculatorService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class LoanInfoCalculatorServiceTest {

    @Autowired
    lateinit var loanCalculatorService: LoanCalculatorService

    @Test
    fun `test calculateLoan with single rate`() {
        val request = LoanRequest(
            loanAmount = 100,
            loanPeriod = 36,
            isSingleRate = true,
            interestRate = 1.0,
            ratePeriods = null,
            gracePeriod = 3,
            relatedFees = 10000
        )

        val response = loanCalculatorService.calculateLoan(request)

        assertEquals(request.loanAmount, response.loanAmount)
        assertEquals(1.61, response.totalApr)

        // 驗證第1期數據
        val paymentFirst = response.payments[0]
        assertEquals(1, paymentFirst.period)
        assertEquals(0, paymentFirst.principalForPeriod)
        assertEquals(833, paymentFirst.interestForPeriod)
        assertEquals(833, paymentFirst.monthlyPayment)
        assertEquals(1000000, paymentFirst.remainingPrincipal)
        assertEquals(833, paymentFirst.totalInterestAccrued)

        // 驗證過寬限期後的第1期數據
        val paymentAfterGrace = response.payments[request.gracePeriod]
        assertEquals(4, paymentAfterGrace.period)
        assertEquals(29901, paymentAfterGrace.principalForPeriod)
        assertEquals(833, paymentAfterGrace.interestForPeriod)
        assertEquals(30734, paymentAfterGrace.monthlyPayment)
        assertEquals(970099, paymentAfterGrace.remainingPrincipal)
        assertEquals(3332, paymentAfterGrace.totalInterestAccrued)

        // 驗證最後一期數據
        val paymentLast = response.payments[request.loanPeriod - 1]
        assertEquals(36, paymentLast.period)
        assertEquals(30716, paymentLast.principalForPeriod)
        assertEquals(26, paymentLast.interestForPeriod)
        assertEquals(30742, paymentLast.monthlyPayment)
        assertEquals(0, paymentLast.remainingPrincipal)
        assertEquals(16729, paymentLast.totalInterestAccrued)

    }

    @Test
    fun `test calculateLoan with segmented rate`() {
        val request = LoanRequest(
            loanAmount = 100,
            loanPeriod = 36,
            isSingleRate = false,
            interestRate = null,
            ratePeriods = listOf(
                RatePeriod(rate = 2.0, months = 12),
                RatePeriod(rate = 3.0, months = 24),
            ),
            gracePeriod = 3,
            relatedFees = 10000
        )

        val response = loanCalculatorService.calculateLoan(request)

        assertEquals(request.loanAmount, response.loanAmount)
        assertEquals(3.06,response.totalApr)

        // 驗證第1期數據
        val paymentFirst = response.payments[0]
        assertEquals(1, paymentFirst.period)
        assertEquals(0, paymentFirst.principalForPeriod)
        assertEquals(1667, paymentFirst.interestForPeriod)
        assertEquals(1667, paymentFirst.monthlyPayment)
        assertEquals(1000000, paymentFirst.remainingPrincipal)
        assertEquals(1667, paymentFirst.totalInterestAccrued)

        // 驗證過寬限期後的第1期數據
        val paymentAfterGrace = response.payments[request.gracePeriod]
        assertEquals(4, paymentAfterGrace.period)
        assertEquals(29502, paymentAfterGrace.principalForPeriod)
        assertEquals(1667, paymentAfterGrace.interestForPeriod)
        assertEquals(31169, paymentAfterGrace.monthlyPayment)
        assertEquals(970498, paymentAfterGrace.remainingPrincipal)
        assertEquals(6668, paymentAfterGrace.totalInterestAccrued)

        // 驗證分段式的第1期數據
        val paymentSegmentStart = response.payments[12]
        assertEquals(13, paymentSegmentStart.period)
        assertEquals(29660, paymentSegmentStart.principalForPeriod)
        assertEquals(1832, paymentSegmentStart.interestForPeriod)
        assertEquals(31492, paymentSegmentStart.monthlyPayment)
        assertEquals(703041, paymentSegmentStart.remainingPrincipal)
        assertEquals(20055, paymentSegmentStart.totalInterestAccrued)

        // 驗證最後一期數據
        val paymentLast = response.payments[request.loanPeriod - 1]
        assertEquals(36, paymentLast.period)
        assertEquals(31425, paymentLast.principalForPeriod)
        assertEquals(79, paymentLast.interestForPeriod)
        assertEquals(31504, paymentLast.monthlyPayment)
        assertEquals(0, paymentLast.remainingPrincipal)
        assertEquals(41342, paymentLast.totalInterestAccrued)
    }
}