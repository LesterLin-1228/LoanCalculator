package org.example.loancalculator.controller

import jakarta.validation.Valid
import org.example.loancalculator.dto.repayment.EarlyPrincipalRepayReq
import org.example.loancalculator.dto.repayment.RepaymentReq
import org.example.loancalculator.dto.repayment.EarlyPrincipalRepayDto
import org.example.loancalculator.dto.repayment.RepaymentDto
import org.example.loancalculator.service.impl.RepaymentServiceImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/repayments")
class RepaymentController(@Autowired private val repaymentServiceImpl: RepaymentServiceImpl) {

    @PostMapping("/repay")
    fun repay(@RequestBody @Valid repaymentReq: RepaymentReq): RepaymentDto {
        return repaymentServiceImpl.repay(repaymentReq)
    }

    @PostMapping("/calculateEarlyPrincipalRepay")
    fun calculateEarlyPrincipalRepay(
        @RequestBody @Valid earlyPrincipalRepayReq: EarlyPrincipalRepayReq
    ): EarlyPrincipalRepayDto {
        return repaymentServiceImpl.calculateEarlyPrincipalRepay(earlyPrincipalRepayReq)
    }

    @PostMapping("/earlyPrincipalRepay")
    fun earlyPrincipalRepay(
        @RequestBody @Valid earlyPrincipalRepayReq: EarlyPrincipalRepayReq
    ): EarlyPrincipalRepayDto {
        return repaymentServiceImpl.earlyPrincipalRepay(earlyPrincipalRepayReq)
    }
}