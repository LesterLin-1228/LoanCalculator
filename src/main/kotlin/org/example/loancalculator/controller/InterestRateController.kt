package org.example.loancalculator.controller

import org.example.loancalculator.dto.interestRate.CreateInterestRateReq
import org.example.loancalculator.dto.interestRate.InterestRateDto
import org.example.loancalculator.entity.InterestRate
import org.example.loancalculator.service.InterestRateService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/interest-rate")
class InterestRateController(@Autowired private val interestRateService: InterestRateService) {

    @PostMapping
    fun createInterestRate(@RequestBody createInterestRateReq: CreateInterestRateReq): InterestRateDto {
        return interestRateService.createInterestRate(createInterestRateReq)
    }

    @GetMapping("/latest")
    fun getLatestInterestRate(): ResponseEntity<InterestRate> {
        val latestInterestRate = interestRateService.getLatestInterestRate()
        return if (latestInterestRate != null) {
            ResponseEntity(latestInterestRate, HttpStatus.OK)
        } else {
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }
}