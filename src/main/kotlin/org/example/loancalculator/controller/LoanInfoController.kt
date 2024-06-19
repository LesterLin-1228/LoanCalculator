package org.example.loancalculator.controller

import jakarta.validation.Valid
import org.example.loancalculator.dto.loanInfo.LoanDetailsDto
import org.example.loancalculator.dto.loanInfo.LoanInfoDto
import org.example.loancalculator.dto.loanInfo.LoanInfoReq
import org.example.loancalculator.dto.loanInfo.LoanStatisticsDto
import org.example.loancalculator.service.impl.LoanInfoServiceImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/loanInfo")
class LoanInfoController(@Autowired private val loanInfoServiceImpl: LoanInfoServiceImpl) {

    @PostMapping("/create-loan")
    fun createLoan(@RequestBody @Valid loanInfoReq: LoanInfoReq): LoanInfoDto {
        return loanInfoServiceImpl.createLoan(loanInfoReq)
    }

    @GetMapping("/{loanAccount}")
    fun getLoanDetails(@PathVariable loanAccount: String): LoanDetailsDto {
        return loanInfoServiceImpl.getLoanDetails(loanAccount)
    }

    @GetMapping("/statistics")
    fun getLoanStatistics(): LoanStatisticsDto{
        return loanInfoServiceImpl.getLoanStatistics()
    }
}