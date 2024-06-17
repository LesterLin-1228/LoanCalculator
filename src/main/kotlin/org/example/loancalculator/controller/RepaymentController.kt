package org.example.loancalculator.controller

import jakarta.validation.Valid
import org.example.loancalculator.dto.repayment.EarlyPrincipalRepayReq
import org.example.loancalculator.dto.repayment.RepaymentReq
import org.example.loancalculator.dto.repayment.EarlyPrincipalRepayDto
import org.example.loancalculator.dto.repayment.RepaymentDto
import org.example.loancalculator.service.RepaymentService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/repayments")
class RepaymentController(@Autowired private val repaymentService: RepaymentService) {

    @PostMapping
    fun repay(@RequestBody @Valid repaymentReq: RepaymentReq): RepaymentDto {
        val repaymentDto = repaymentService.repay(repaymentReq)
        return repaymentDto
    }

    @PostMapping("/calculateEarlyPrincipalRepay")
    fun calculateEarlyPrincipalRepay(
        @RequestBody @Valid earlyPrincipalRepayReq: EarlyPrincipalRepayReq
    ): EarlyPrincipalRepayDto {
        val earlyPrincipalRepayDto = repaymentService.calculateEarlyPrincipalRepay(earlyPrincipalRepayReq)
        return earlyPrincipalRepayDto
    }

    @PostMapping("/earlyPrincipalRepay")
    fun earlyPrincipalRepay(
        @RequestBody @Valid earlyPrincipalRepayReq: EarlyPrincipalRepayReq
    ): EarlyPrincipalRepayDto {
        val earlyPrincipalRepayDto = repaymentService.earlyPrincipalRepay(earlyPrincipalRepayReq)
        return earlyPrincipalRepayDto
    }
}