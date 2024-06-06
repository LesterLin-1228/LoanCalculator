package org.example.loancalculator.controller

import org.example.loancalculator.dto.InterestRateDto
import org.example.loancalculator.entity.InterestRate
import org.example.loancalculator.service.InterestRateService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/interest-rate")
class InterestRateController(@Autowired private val interestRateService: InterestRateService) {

    @PostMapping
    fun createDefaultInterestRate(@RequestBody interestRateDto: InterestRateDto): ResponseEntity<String> {
        val result = interestRateService.createDefaultInterestRate(interestRateDto)
        return ResponseEntity(result.message, result.status)
    }
}