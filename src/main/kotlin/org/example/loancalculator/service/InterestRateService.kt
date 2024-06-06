package org.example.loancalculator.service

import org.example.loancalculator.dao.InterestRateDao
import org.example.loancalculator.dto.InterestRateDto
import org.example.loancalculator.entity.InterestRate
import org.example.loancalculator.result.Result
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class InterestRateService(@Autowired private val interestRateDao: InterestRateDao) {

    fun createDefaultInterestRate(interestRateDto: InterestRateDto): Result<Any?> {
        val interestRate = InterestRate(
            date = interestRateDto.date ?: LocalDate.now(),
            baseRate = interestRateDto.baseRate ?: 2.0
        )
        interestRateDao.save(interestRate)
        return Result("成功建立", HttpStatus.CREATED)
    }

    fun getLatestInterestRate(): InterestRate? {
        return interestRateDao.findFirstByOrderByDateDesc()
    }
}