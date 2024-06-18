package org.example.loancalculator.controller

import org.example.loancalculator.dto.interestRate.AdjustInterestRateReq
import org.example.loancalculator.dto.interestRate.CreateInterestRateReq
import org.example.loancalculator.dto.interestRate.InterestRateDto
import org.example.loancalculator.service.InterestRateService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/interest-rate")
class InterestRateController(@Autowired private val interestRateService: InterestRateService) {

    @PostMapping
    fun createInterestRate(@RequestBody createInterestRateReq: CreateInterestRateReq): InterestRateDto {
        return interestRateService.createInterestRate(createInterestRateReq)
    }

    @GetMapping("/latest")
    fun getLatestInterestRate(): InterestRateDto {
        return interestRateService.getLatestInterestRate()
    }

    @PostMapping("/adjustInterestRate")
    fun adjustInterestRate(@RequestBody adjustInterestRateReq: AdjustInterestRateReq): InterestRateDto {
        return interestRateService.adjustInterestRate(adjustInterestRateReq)
    }
}