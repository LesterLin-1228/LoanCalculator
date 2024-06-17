package org.example.loancalculator.service

import org.example.loancalculator.dao.InterestRateDao
import org.example.loancalculator.dto.error.ErrorResponse
import org.example.loancalculator.dto.interestRate.CreateInterestRateReq
import org.example.loancalculator.dto.interestRate.InterestRateDto
import org.example.loancalculator.entity.InterestRate
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
@ActiveProfiles("test") // 使用測試配置文件 application-test.yml
class InterestRateServiceTest {

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

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
        val response = restTemplate.postForEntity("/interest-rate", createInterestRateReq, InterestRateDto::class.java)

        // Assert
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(LocalDate.of(2024, 6, 1), response.body?.date)
        assertEquals(2.5, response.body?.baseRate)
    }

    @Test
    fun `createInterestRate should return conflict when date is duplicated`() {
        val date = LocalDate.of(2024, 6, 1)
        interestRateDao.save(InterestRate(date, baseRate = 2.5))

        val createInterestRateReq = CreateInterestRateReq(
            date = date,
            baseRate = 2.3
        )

        val response = restTemplate.postForEntity("/interest-rate", createInterestRateReq, ErrorResponse::class.java)

        assertEquals(HttpStatus.CONFLICT, response.statusCode)
        assertNotNull(response.body)
        assertEquals("該日期的基礎利率已存在", response.body?.message)
        assertEquals("/interest-rate", response.body?.path)
        assertEquals(409, response.body?.status)
    }

    @Test
    fun `getLatestInterestRate should return latest base rate`() {
        val olderDate = LocalDate.of(2024, 5, 1)
        val latestDate = LocalDate.of(2024, 6, 1)
        interestRateDao.save(InterestRate(olderDate, 2.0))
        interestRateDao.save(InterestRate(latestDate, 2.5))

        val response = restTemplate.getForEntity("/interest-rate/latest", InterestRateDto::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
        println(response.body)
        assertEquals(LocalDate.of(2024, 6, 1), response.body?.date)
        assertEquals(2.5, response.body?.baseRate)
    }
}