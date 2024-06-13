package org.example.loancalculator.service

import org.example.loancalculator.dao.InterestRateDao
import org.example.loancalculator.dto.interestRate.CreateInterestRateReq
import org.example.loancalculator.dto.interestRate.InterestRateDto
import org.example.loancalculator.entity.InterestRate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class InterestRateService(@Autowired private val interestRateDao: InterestRateDao) {

    fun createInterestRate(createInterestRateReq: CreateInterestRateReq): InterestRateDto {
        val date = createInterestRateReq.date ?: LocalDate.now()
        val baseRate = createInterestRateReq.baseRate ?: 2.0

        // 檢查資料庫是否有重複的日期
        if (interestRateDao.existsByDate(date)) {
            throw Exception("該日期的基礎利率已存在")
        }
        val interestRate = InterestRate(
            date = date,
            baseRate = baseRate
        )
        interestRateDao.save(interestRate)

        return InterestRateDto(date = interestRate.date, baseRate = interestRate.baseRate)
    }

    fun getLatestInterestRate(): InterestRate? {
        return interestRateDao.findFirstByOrderByDateDesc()
    }
}