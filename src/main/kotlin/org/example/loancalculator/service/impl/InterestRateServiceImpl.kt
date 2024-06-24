package org.example.loancalculator.service.impl

import org.example.loancalculator.dao.InterestRateDao
import org.example.loancalculator.dto.interestRate.AdjustInterestRateReq
import org.example.loancalculator.dto.interestRate.CreateInterestRateReq
import org.example.loancalculator.dto.interestRate.InterestRateDto
import org.example.loancalculator.entity.InterestRate
import org.example.loancalculator.service.InterestRateService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.math.BigDecimal
import java.time.LocalDate

@Service
class InterestRateServiceImpl(@Autowired private val interestRateDao: InterestRateDao) : InterestRateService {

    override fun createInterestRate(createInterestRateReq: CreateInterestRateReq): InterestRateDto {
        val date = createInterestRateReq.date
        val baseRate = createInterestRateReq.baseRate

        // 檢查資料庫是否有重複的日期
        if (interestRateDao.existsByDate(date)) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "該日期的基礎利率已存在")
        }

        val interestRate = InterestRate(
            date = date,
            baseRate = baseRate
        )
        interestRateDao.save(interestRate)

        val interestRateDto = InterestRateDto(
            date = interestRate.date,
            baseRate = interestRate.baseRate
        )

        return interestRateDto
    }

    override fun getLatestInterestRate(): InterestRateDto {
        // 先查詢當天的基礎利率
        val interestRateToday = interestRateDao.findByDate(LocalDate.now())

        // 如果當天的基礎利率存在，返回當天的基礎利率
        if (interestRateToday != null) {
            return InterestRateDto(
                date = interestRateToday.date,
                baseRate = interestRateToday.baseRate
            )
        }

        val latestInterestRate =
            interestRateDao.findFirstByDateBeforeOrderByDateDesc(LocalDate.now()) ?: throw ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "查無基礎利率"
            )

        val interestRateDto = InterestRateDto(
            date = latestInterestRate.date,
            baseRate = latestInterestRate.baseRate
        )

        return interestRateDto
    }

    override fun adjustInterestRate(adjustInterestRateReq: AdjustInterestRateReq): InterestRateDto {
        val latestInterestRate =
            interestRateDao.findByDate(adjustInterestRateReq.effectiveDate)
                ?: interestRateDao.findFirstByDateBeforeOrderByDateDesc(adjustInterestRateReq.effectiveDate)
                ?: throw ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "查無基礎利率"
                )

        // 使用 BigDecimal 計算避免精準度丟失
        val baseRate = BigDecimal.valueOf(latestInterestRate.baseRate)
        val adjustmentRate = BigDecimal.valueOf(adjustInterestRateReq.adjustmentRate)
        val newBaseRate = baseRate.add(adjustmentRate)

        if (newBaseRate <= BigDecimal.ZERO) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "調整後的基礎利率不能為負數或0")
        }

        val interestRate = InterestRate(
            date = adjustInterestRateReq.effectiveDate,
            baseRate = newBaseRate.toDouble()
        )
        interestRateDao.save(interestRate)

        val interestRateDto = InterestRateDto(
            date = interestRate.date,
            baseRate = interestRate.baseRate
        )

        return interestRateDto
    }
}