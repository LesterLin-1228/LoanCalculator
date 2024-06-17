package org.example.loancalculator.service

import org.example.loancalculator.dao.InterestRateDao
import org.example.loancalculator.dao.LoanInfoDao
import org.example.loancalculator.dao.LoanInterestRateDao
import org.example.loancalculator.dao.RepaymentRecordDao
import org.example.loancalculator.dto.error.ErrorResponse
import org.example.loancalculator.dto.repayment.EarlyPrincipalRepayDto
import org.example.loancalculator.dto.repayment.EarlyPrincipalRepayReq
import org.example.loancalculator.dto.repayment.RepaymentDto
import org.example.loancalculator.dto.repayment.RepaymentReq
import org.example.loancalculator.entity.InterestRate
import org.example.loancalculator.entity.LoanInfo
import org.example.loancalculator.entity.LoanInterestRate
import org.junit.jupiter.api.Assertions.*
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
class RepaymentServiceTest {

    @Autowired
    private lateinit var testRestTemplate: TestRestTemplate

    @Autowired
    private lateinit var repaymentRecordDao: RepaymentRecordDao

    @Autowired
    private lateinit var loanInterestRateDao: LoanInterestRateDao

    @Autowired
    private lateinit var interestRateDao: InterestRateDao

    @Autowired
    private lateinit var loanInfoDao: LoanInfoDao

    @BeforeEach
    fun setUp() {
        // 因為資料庫的參考完整性約束問題，所以需要先刪除相關的 RepaymentRecord 記錄
        // 或是一開始就在實體類配置級聯刪除，cascade = [CascadeType.ALL]
        repaymentRecordDao.deleteAll()
        loanInterestRateDao.deleteAll()
        interestRateDao.deleteAll()
        loanInfoDao.deleteAll()

        // 初始化測試數據
        val loanInfo = LoanInfo(
            loanAccount = "111",
            startDate = LocalDate.now(),
            endDate = LocalDate.now().plusMonths(36),
            repaymentDueDay = LocalDate.now().dayOfMonth,
            principalBalance = 100000,
            loanAmount = 100000,
            loanTerm = 36
        )
        loanInfoDao.save(loanInfo)

        val interestRate = InterestRate(
            date = LocalDate.now(),
            baseRate = 2.0
        )
        interestRateDao.save(interestRate)

        val loanInterestRate = LoanInterestRate(
            loanAccount = "111",
            rateDifference = 0.5,
            rateStartDate = LocalDate.now()
        )
        loanInterestRateDao.save(loanInterestRate)
    }

    @Test
    fun `repay should update loan information and create repayment record`() {
        val repaymentReq = RepaymentReq(
            loanAccount = "111",
            repaymentAmount = 10000
        )

        val response = testRestTemplate.postForEntity("/repayments", repaymentReq, RepaymentDto::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
        assertEquals(10000, response.body?.repaymentAmount)

        // 驗證 loanInfo 數據是否更新正確
        val updateLoanInfo = loanInfoDao.findByLoanAccount("111")
        assertNotNull(updateLoanInfo)
        assertEquals(10000, updateLoanInfo!!.totalAmountRepayment)
        assertTrue(updateLoanInfo.totalInterestRepayment > 0)
        assertTrue(updateLoanInfo.totalPrincipalRepayment > 0)

        // 驗證是否創建了還款記錄
        val repaymentRecords = repaymentRecordDao.findByLoanInfo(updateLoanInfo)
        assertTrue(repaymentRecords.isNotEmpty())
        val repaymentRecord = repaymentRecords.first()
        assertEquals(10000, repaymentRecord.repaymentAmount)
        assertEquals(LocalDate.now(), repaymentRecord.repaymentDate)
        assertTrue(repaymentRecord.principalRepaid > 0)
        assertTrue(repaymentRecord.interestRepaid > 0)
        assertEquals(2.5, repaymentRecord.currentInterestRate)
    }

    @Test
    fun `repay should return not found when loan account does not exist`() {
        val repaymentReq = RepaymentReq(
            loanAccount = "222",
            repaymentAmount = 10000
        )

        val response = testRestTemplate.postForEntity("/repayments", repaymentReq, ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals("貸款帳號不存在", response.body?.message)
    }

    @Test
    fun `calculateEarlyPrincipalRepay should return correct calculation`() {
        val earlyPrincipalRepayReq = EarlyPrincipalRepayReq(
            loanAccount = "111",
            earlyPrincipalRepayment = 30000
        )

        val response = testRestTemplate.postForEntity(
            "/repayments/calculateEarlyPrincipalRepay",
            earlyPrincipalRepayReq,
            EarlyPrincipalRepayDto::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
        assertEquals(70000, response.body!!.principalBalance)
        assertTrue(response.body?.nextRepaymentAmount!! > 0)
        println(response.body)
    }

    @Test
    fun `earlyPrincipalRepay should return correct dto and update entity`() {
        val earlyPrincipalRepayReq = EarlyPrincipalRepayReq(
            loanAccount = "111",
            earlyPrincipalRepayment = 30000
        )

        val response = testRestTemplate.postForEntity(
            "/repayments/earlyPrincipalRepay",
            earlyPrincipalRepayReq,
            EarlyPrincipalRepayDto::class.java
        )

        assertEquals(70000, response.body?.principalBalance)
        assertTrue(response.body?.nextRepaymentAmount!! > 0)

        // 驗證 loanInfo 數據是否更新正確
        val updateLoanInfo = loanInfoDao.findByLoanAccount("111")
        assertNotNull(updateLoanInfo)
        assertEquals(70000, updateLoanInfo!!.principalBalance)
        assertEquals(30000, updateLoanInfo.totalPrincipalRepayment)
        assertEquals(30000, updateLoanInfo.totalAmountRepayment)

        // 驗證是否創建了還款記錄
        val repaymentRecords = repaymentRecordDao.findByLoanInfo(updateLoanInfo)
        assertTrue(repaymentRecords.isNotEmpty())
        val repaymentRecord = repaymentRecords.first()
        assertEquals(30000, repaymentRecord.repaymentAmount)
        assertEquals(30000, repaymentRecord.principalRepaid)
        assertEquals(0, repaymentRecord.interestRepaid)
        assertEquals(LocalDate.now(), repaymentRecord.repaymentDate)
        assertEquals(2.5, repaymentRecord.currentInterestRate)
    }
}