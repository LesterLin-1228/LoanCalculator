package org.example.loancalculator.controller

import jakarta.validation.Valid
import org.example.loancalculator.dto.EarlyPrincipalRepaymentDto
import org.example.loancalculator.dto.RepaymentDto
import org.example.loancalculator.response.EarlyPrincipalRepaymentResponse
import org.example.loancalculator.response.Response
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
        val response = repaymentService.repay(repaymentDto)
        return ResponseEntity(response.message, response.status)
    }

    @PostMapping("/calculateEarlyPrincipalRepayment")
    fun calculateEarlyPrincipalRepayment(
        @RequestBody @Valid earlyPrincipalRepaymentDto: EarlyPrincipalRepaymentDto
    ): ResponseEntity<Response<EarlyPrincipalRepaymentResponse>> {
        val response = repaymentService.calculateEarlyPrincipalRepayment(earlyPrincipalRepaymentDto)
        return ResponseEntity(response, response.status)
    }
}