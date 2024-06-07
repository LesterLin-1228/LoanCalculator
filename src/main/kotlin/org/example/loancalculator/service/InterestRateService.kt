package org.example.loancalculator.service

import org.example.loancalculator.dao.InterestRateDao
import org.example.loancalculator.dto.InterestRateDto
import org.example.loancalculator.entity.InterestRate
import org.example.loancalculator.result.Response
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class InterestRateService(@Autowired private val interestRateDao: InterestRateDao) {

    fun createInterestRate(interestRateDto: InterestRateDto): Response<Any?> {
        val date = interestRateDto.date ?: LocalDate.now()
        val baseRate = interestRateDto.baseRate ?: 2.0

        // 檢查資料庫是否有重複的日期
        if (interestRateDao.existsByDate(date)) {
            return Response("該日期的基礎利率已存在", HttpStatus.CONFLICT)
        }
        val interestRate = InterestRate(
            date = date,
            baseRate = baseRate
        )
        interestRateDao.save(interestRate)
        return Response("成功建立", HttpStatus.CREATED)
    }

    fun getLatestInterestRate(): InterestRate? {
        return interestRateDao.findFirstByOrderByDateDesc()
    }
}