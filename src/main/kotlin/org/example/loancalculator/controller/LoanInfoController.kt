package org.example.loancalculator.controller

import jakarta.validation.Valid
import org.example.loancalculator.dto.LoanInfoDto
import org.example.loancalculator.result.LoanDetailsResponse
import org.example.loancalculator.result.Response
import org.example.loancalculator.service.LoanInfoService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/loanInfo")
class LoanInfoController(@Autowired private val loanInfoService: LoanInfoService) {

    @PostMapping
    fun createLoan(@RequestBody @Valid loanInfoDto: LoanInfoDto): ResponseEntity<String> {
        val result = loanInfoService.createLoan(loanInfoDto)
        return ResponseEntity(result.message, result.status)
    }

    @GetMapping("/{loanAccount}")
    fun getLoanDetails(@PathVariable loanAccount: String): ResponseEntity<Response<LoanDetailsResponse>> {
        val result = loanInfoService.getLoanDetails(loanAccount)
        return ResponseEntity(result, result.status)
    }
}