package org.example.loancalculator.service

import org.example.loancalculator.dao.InterestRateDao
import org.example.loancalculator.dao.LoanInfoDao
import org.example.loancalculator.dao.LoanInterestRateDao
import org.example.loancalculator.dto.error.ErrorResponse
import org.example.loancalculator.dto.loanInfo.LoanInfoReq
import org.example.loancalculator.entity.InterestRate
import org.example.loancalculator.entity.LoanInfo
import org.example.loancalculator.entity.LoanInterestRate
import org.example.loancalculator.dto.loanInfo.LoanDetailsDto
import org.example.loancalculator.dto.loanInfo.LoanInfoDto
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDate

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class LoanInfoServiceTest {

    @Autowired
    private lateinit var interestRateDao: InterestRateDao

    @Autowired
    private lateinit var loanInfoDao: LoanInfoDao

    @Autowired
    private lateinit var loanInterestRateDao: LoanInterestRateDao

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @BeforeEach
    fun setUp() {
        loanInterestRateDao.deleteAll()
        loanInfoDao.deleteAll()
    }

    @Test
    fun `createLoan should create new loan when account is not duplicated`() {
        val loanInfoReq = LoanInfoReq(
            loanAccount = "111",
            loanAmount = 1000000,
            loanTerm = 36,
            rateDifference = 0.5
        )

        val response = restTemplate.postForEntity("/loanInfo", loanInfoReq, LoanInfoDto::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
        println(response.body)
    }

    @Test
    fun `createLoan should return conflict when account is duplicated`() {
        val loanInfo = LoanInfo(
            loanAccount = "111",
            startDate = LocalDate.now(),
            endDate = LocalDate.now().plusMonths(36),
            repaymentDueDay = LocalDate.now().dayOfMonth,
            principalBalance = 1000000,
            loanAmount = 1000000,
            loanTerm = 36
        )
        loanInfoDao.save(loanInfo)

        val loanInfoReq = LoanInfoReq(
            loanAccount = "111",
            loanAmount = 1000000,
            loanTerm = 36,
            rateDifference = 0.5
        )

        val response = restTemplate.postForEntity("/loanInfo", loanInfoReq, ErrorResponse::class.java)

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
            principalBalance = 1000000,
            loanAmount = 1000000,
            loanTerm = 36
        )
        loanInfoDao.save(loanInfo)

        interestRateDao.save(InterestRate(date = LocalDate.now(), baseRate = 2.0))

        val loanInterestRate = LoanInterestRate(
            loanAccount = loanInfo.loanAccount,
            rateStartDate = LocalDate.now(),
            rateDifference = 0.5
        )
        loanInterestRateDao.save(loanInterestRate)

        val response = restTemplate.getForEntity("/loanInfo/${loanInfo.loanAccount}", LoanDetailsDto::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
        println(response.body)
        assertEquals(1000000, response.body?.principalBalance)

        // 驗證下次還款日
        val expectedNextRepaymentDate = loanInfo.startDate.plusMonths(1).withDayOfMonth(loanInfo.repaymentDueDay)
        assertEquals(expectedNextRepaymentDate, response.body?.nextRepaymentDate)
    }
}