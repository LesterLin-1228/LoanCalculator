package org.example.loancalculator.controller

import jakarta.validation.Valid
import org.example.loancalculator.dto.RepaymentDto
import org.example.loancalculator.service.RepaymentService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/repayments")
class RepaymentController(@Autowired private val repaymentService: RepaymentService) {

    @PostMapping
    fun repay(@RequestBody @Valid repaymentDto: RepaymentDto): ResponseEntity<String> {
        return repaymentService.repay(repaymentDto)
    }
}