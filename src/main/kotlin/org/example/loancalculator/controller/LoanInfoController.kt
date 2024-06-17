package org.example.loancalculator.controller

import jakarta.validation.Valid
import org.example.loancalculator.dto.loanInfo.LoanDetailsDto
import org.example.loancalculator.dto.loanInfo.LoanInfoDto
import org.example.loancalculator.dto.loanInfo.LoanInfoReq
import org.example.loancalculator.service.LoanInfoService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/loanInfo")
class LoanInfoController(@Autowired private val loanInfoService: LoanInfoService) {

    @PostMapping
    fun createLoan(@RequestBody @Valid loanInfoReq: LoanInfoReq): LoanInfoDto {
        val loanInfoDto = loanInfoService.createLoan(loanInfoReq)
        return loanInfoDto
    }

    @GetMapping("/{loanAccount}")
    fun getLoanDetails(@PathVariable loanAccount: String): LoanDetailsDto {
        val loanDetailsDto = loanInfoService.getLoanDetails(loanAccount)
        return loanDetailsDto
    }
}