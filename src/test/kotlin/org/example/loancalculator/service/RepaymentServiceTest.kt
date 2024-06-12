package org.example.loancalculator.service

import org.example.loancalculator.dao.InterestRateDao
import org.example.loancalculator.dao.LoanInfoDao
import org.example.loancalculator.dao.LoanInterestRateDao
import org.example.loancalculator.dao.RepaymentRecordDao
import org.example.loancalculator.dto.RepaymentDto
import org.example.loancalculator.entity.InterestRate
import org.example.loancalculator.entity.LoanInfo
import org.example.loancalculator.entity.LoanInterestRate
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
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
        loanInfoDao.deleteAll()
        interestRateDao.deleteAll()
        loanInterestRateDao.deleteAll()
        repaymentRecordDao.deleteAll()

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
        val repaymentDto = RepaymentDto(
            loanAccount = "111",
            repaymentAmount = 10000
        )

        val response = testRestTemplate.postForEntity("/repayments", repaymentDto, String::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals("還款操作成功", response.body)

        // 驗證 loanInfo 數據是否更新正確
        val updateLoanInfo = loanInfoDao.findByLoanAccount("111")
        assertNotNull(updateLoanInfo)
        assertEquals(10000, updateLoanInfo?.totalAmountRepaid)
        assertTrue(updateLoanInfo?.totalInterestRepaid!! > 0)
        assertTrue(updateLoanInfo.totalPrincipalRepaid > 0)

        // 驗證是否創建了還款記錄
        val repaymentRecords = repaymentRecordDao.findByLoanAccount("111")
        assertTrue(repaymentRecords.isNotEmpty())
        val repaymentRecord = repaymentRecords.first()
        assertEquals(10000, repaymentRecord.repaymentAmount)
        assertEquals(LocalDate.now(), repaymentRecord.repaymentDate)
        assertTrue(repaymentRecord.principalRepaid > 0)
        assertTrue(repaymentRecord.interestRepaid > 0)
        assertEquals(2.5,repaymentRecord.currentInterestRate)
    }

    @Test
    fun `repay should return bad request when loan account does not exist`() {
        val repaymentDto = RepaymentDto(
            loanAccount = "222",
            repaymentAmount = 10000
        )

        val response = testRestTemplate.postForEntity("/repayments", repaymentDto, String::class.java)

        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertEquals("貸款帳號不存在", response.body)
    }
}