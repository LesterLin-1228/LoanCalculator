package org.example.loancalculator.controller

import jakarta.validation.Valid
import org.example.loancalculator.dto.interestRate.AdjustInterestRateReq
import org.example.loancalculator.dto.interestRate.CreateInterestRateReq
import org.example.loancalculator.dto.interestRate.InterestRateDto
import org.example.loancalculator.service.impl.InterestRateServiceImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/interest-rate")
class InterestRateController(@Autowired private val interestRateServiceImpl: InterestRateServiceImpl) {

    @PostMapping
    fun createInterestRate(@RequestBody @Valid createInterestRateReq: CreateInterestRateReq): InterestRateDto {
        return interestRateServiceImpl.createInterestRate(createInterestRateReq)
    }

    @GetMapping("/latest")
    fun getLatestInterestRate(): InterestRateDto {
        return interestRateServiceImpl.getLatestInterestRate()
    }

    @PostMapping("/adjustInterestRate")
    fun adjustInterestRate(@RequestBody @Valid adjustInterestRateReq: AdjustInterestRateReq): InterestRateDto {
        return interestRateServiceImpl.adjustInterestRate(adjustInterestRateReq)
    }
}