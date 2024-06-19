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
        val latestInterestRate = interestRateDao.findFirstByOrderByDateDesc() ?: throw ResponseStatusException(
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
        val latestInterestRate = interestRateDao.findFirstByOrderByDateDesc() ?: throw ResponseStatusException(
            HttpStatus.NOT_FOUND,
            "查無基礎利率"
        )

        // 計算新的基礎利率
        val newBaseRate = latestInterestRate.baseRate + adjustInterestRateReq.adjustmentRate

        if (newBaseRate <= 0) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "調整後的基礎利率不能為負數或0")
        }

        val interestRate = InterestRate(
            date = latestInterestRate.date,
            baseRate = newBaseRate
        )
        interestRateDao.save(interestRate)

        val interestRateDto = InterestRateDto(
            date = interestRate.date,
            baseRate = interestRate.baseRate
        )

        return interestRateDto
    }
}