package org.example.loancalculator.service

import org.example.loancalculator.dao.InterestRateDao
import org.example.loancalculator.dto.error.ErrorResponse
import org.example.loancalculator.dto.interestRate.AdjustInterestRateReq
import org.example.loancalculator.dto.interestRate.CreateInterestRateReq
import org.example.loancalculator.dto.interestRate.InterestRateDto
import org.example.loancalculator.entity.InterestRate
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDate

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test") // 使用測試配置文件 application-test.yml
class InterestRateServiceImplTest {

    @Autowired
    private lateinit var testRestTemplate: TestRestTemplate

    @Autowired
    private lateinit var interestRateDao: InterestRateDao

    @BeforeEach
    fun setUp() {
        // 在每次測試之前清理資料庫
        interestRateDao.deleteAll()
    }

    @Test
    fun `createInterestRate should create new interest rate when date is not duplicated`() {
        // Arrange
        val createInterestRateReq = CreateInterestRateReq(
            date = LocalDate.of(2024, 6, 1),
            baseRate = 2.5
        )

        // Act
        val response =
            testRestTemplate.postForEntity("/interest-rate", createInterestRateReq, InterestRateDto::class.java)

        // Assert
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(LocalDate.of(2024, 6, 1), response.body?.date)
        assertEquals(2.5, response.body?.baseRate)
    }

    @Test
    fun `createInterestRate should return conflict when date is duplicated`() {
        interestRateDao.save(
            InterestRate(
                LocalDate.of(2024, 6, 1),
                baseRate = 2.5
            )
        )

        val createInterestRateReq = CreateInterestRateReq(
            date = LocalDate.of(2024, 6, 1),
            baseRate = 2.3
        )

        val response =
            testRestTemplate.postForEntity("/interest-rate", createInterestRateReq, ErrorResponse::class.java)

        assertEquals(HttpStatus.CONFLICT, response.statusCode)
        assertEquals("該日期的基礎利率已存在", response.body?.message)
    }

    @Test
    fun `getLatestInterestRate should return latest base rate`() {
        val olderDate = LocalDate.of(2024, 5, 1)
        val latestDate = LocalDate.now()
        interestRateDao.save(InterestRate(olderDate, 2.0))
        interestRateDao.save(InterestRate(latestDate, 2.5))

        val response = testRestTemplate.getForEntity("/interest-rate/latest", InterestRateDto::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(latestDate, response.body?.date)
        assertEquals(2.5, response.body?.baseRate)
    }

    @Test
    fun `adjustInterestRate should return new interest rate and date when new interest base rate is positive`() {
        interestRateDao.save(InterestRate(LocalDate.now(), baseRate = 2.5))

        val adjustInterestRateReq = AdjustInterestRateReq(
            adjustmentRate = 0.5,
            effectiveDate = LocalDate.of(2024,7,15)
        )

        val response = testRestTemplate.postForEntity(
            "/interest-rate/adjustInterestRate",
            adjustInterestRateReq,
            InterestRateDto::class.java
        )

        assertEquals(LocalDate.of(2024,7,15), response.body?.date)
        assertEquals(3.0, response.body?.baseRate)
    }

    @Test
    fun `adjustInterestRate should return bad request when new interest base rate is negative or zero`() {
        interestRateDao.save(InterestRate(LocalDate.now(), baseRate = 2.5))

        val adjustInterestRateReq = AdjustInterestRateReq(
            adjustmentRate = -2.5,
            effectiveDate = LocalDate.of(2024,7,15)
        )

        val response = testRestTemplate.postForEntity(
            "/interest-rate/adjustInterestRate",
            adjustInterestRateReq,
            ErrorResponse::class.java
        )

        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertEquals("調整後的基礎利率不能為負數或0", response.body?.message)
    }
}