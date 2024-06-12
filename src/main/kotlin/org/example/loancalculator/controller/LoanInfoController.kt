package org.example.loancalculator.controller

import jakarta.validation.Valid
import org.example.loancalculator.dto.LoanInfoDto
import org.example.loancalculator.response.LoanDetailsResponse
import org.example.loancalculator.service.LoanInfoService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/loanInfo")
class LoanInfoController(@Autowired private val loanInfoService: LoanInfoService) {

    @PostMapping
    fun createLoan(@RequestBody @Valid loanInfoDto: LoanInfoDto): ResponseEntity<String> {
        val response = loanInfoService.createLoan(loanInfoDto)
        return ResponseEntity(response.message, response.status)
    }

    @GetMapping("/{loanAccount}")
    fun getLoanDetails(@PathVariable loanAccount: String): ResponseEntity<LoanDetailsResponse> {
        val response = loanInfoService.getLoanDetails(loanAccount)
        return ResponseEntity(response.data, response.status)
    }
}