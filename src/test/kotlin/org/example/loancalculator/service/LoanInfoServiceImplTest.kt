package org.example.loancalculator.service

import org.example.loancalculator.dao.InterestRateDao
import org.example.loancalculator.dao.LoanInfoDao
import org.example.loancalculator.dao.LoanInterestRateDao
import org.example.loancalculator.dto.error.ErrorResponse
import org.example.loancalculator.dto.loanInfo.LoanDetailsDto
import org.example.loancalculator.dto.loanInfo.LoanInfoDto
import org.example.loancalculator.dto.loanInfo.LoanInfoReq
import org.example.loancalculator.dto.loanInfo.LoanStatisticsDto
import org.example.loancalculator.entity.InterestRate
import org.example.loancalculator.entity.LoanInfo
import org.example.loancalculator.entity.LoanInterestRate
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDate
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class LoanInfoServiceImplTest {

    @Autowired
    private lateinit var loanCalculatorService: LoanCalculatorService

    @Autowired
    private lateinit var interestRateDao: InterestRateDao

    @Autowired
    private lateinit var loanInfoDao: LoanInfoDao

    @Autowired
    private lateinit var loanInterestRateDao: LoanInterestRateDao

    @Autowired
    private lateinit var testRestTemplate: TestRestTemplate

    @BeforeEach
    fun setUp() {
        loanInterestRateDao.deleteAll()
        loanInfoDao.deleteAll()
    }

    @Test
    fun `createLoan should create new loan when account is not duplicated`() {
        val loanInfoReq = LoanInfoReq(
            loanAccount = "111",
            loanAmount = 100000,
            loanTerm = 36,
            rateDifference = 0.5
        )

        val response = testRestTemplate.postForEntity("/loanInfo/create-loan", loanInfoReq, LoanInfoDto::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals("111", response.body?.loanAccount)
    }

    @Test
    fun `createLoan should return conflict when account is duplicated`() {
        loanInfoDao.save(
            LoanInfo(
                loanAccount = "111",
                startDate = LocalDate.now(),
                endDate = LocalDate.now().plusMonths(36),
                repaymentDueDay = LocalDate.now().dayOfMonth,
                principalBalance = 100000,
                loanAmount = 100000,
                loanTerm = 36,
                totalAmountRepayment = 0,
                totalPrincipalRepayment = 0,
                totalInterestRepayment = 0
            )
        )

        val loanInfoReq = LoanInfoReq(
            loanAccount = "111",
            loanAmount = 100000,
            loanTerm = 36,
            rateDifference = 0.5
        )

        val response = testRestTemplate.postForEntity("/loanInfo/create-loan", loanInfoReq, ErrorResponse::class.java)

        assertEquals(HttpStatus.CONFLICT, response.statusCode)
        assertEquals("帳號已存在", response.body?.message)
    }

    @Test
    fun `getLoanDetails should return loan details`() {
        val loanInfo = LoanInfo(
            loanAccount = "111",
            startDate = LocalDate.now(),
            endDate = LocalDate.now().plusMonths(36),
            repaymentDueDay = LocalDate.now().dayOfMonth,
            principalBalance = 90000,
            loanAmount = 100000,
            loanTerm = 36,
            totalAmountRepayment = 10000,
            totalPrincipalRepayment = 10000,
            totalInterestRepayment = 0
        )
        loanInfoDao.save(loanInfo)

        interestRateDao.save(InterestRate(date = LocalDate.now(), baseRate = 2.0))

        val loanInterestRate = LoanInterestRate(
            loanAccount = loanInfo.loanAccount,
            rateStartDate = LocalDate.now(),
            rateDifference = 0.5
        )
        loanInterestRateDao.save(loanInterestRate)

        val response = testRestTemplate.getForEntity("/loanInfo/${loanInfo.loanAccount}", LoanDetailsDto::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(90000, response.body?.principalBalance)
        assertTrue(response.body?.nextRepayment!! > 0)

        // 根據剩餘本金查找對應期數
        val loanResponse = loanCalculatorService.prepareLoanRequest(loanInfo)
        val nextRepaymentInfo = loanResponse.payments.firstOrNull { it.principalBalance < loanInfo.principalBalance }
        // 確保查找到的下一期還款資訊不為 null，否則測試失敗並顯示訊息
        assertNotNull(nextRepaymentInfo,"查無下期還款資訊")

        // 驗證下次還款日
        val nextPaymentPeriod = nextRepaymentInfo.period
        val expectedNextRepaymentDate = loanInfo.startDate.plusMonths(nextPaymentPeriod.toLong()).withDayOfMonth(loanInfo.repaymentDueDay)

        assertEquals(expectedNextRepaymentDate, response.body?.nextRepaymentDate)
        println(response.body)
    }

    @Test
    fun `getLoanStatistics should return correct LoanStatisticsDto`() {
        loanInfoDao.save(
            LoanInfo(
                loanAccount = "111",
                startDate = LocalDate.now(),
                endDate = LocalDate.now().plusMonths(36),
                repaymentDueDay = LocalDate.now().dayOfMonth,
                principalBalance = 100000,
                loanAmount = 100000,
                loanTerm = 36,
                totalAmountRepayment = 20000,
                totalPrincipalRepayment = 15000,
                totalInterestRepayment = 5000
            )
        )
        loanInfoDao.save(
            LoanInfo(
                loanAccount = "222",
                startDate = LocalDate.now(),
                endDate = LocalDate.now().plusMonths(36),
                repaymentDueDay = LocalDate.now().dayOfMonth,
                principalBalance = 100000,
                loanAmount = 100000,
                loanTerm = 36,
                totalAmountRepayment = 10000,
                totalPrincipalRepayment = 7000,
                totalInterestRepayment = 3000,
            )
        )

        val response = testRestTemplate.getForEntity("/loanInfo/statistics", LoanStatisticsDto::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(200000, response.body?.totalLoanAmount)
        assertEquals(30000, response.body?.totalRepaidAmount)
        assertEquals(8000, response.body?.totalInterestReceived)
    }
}