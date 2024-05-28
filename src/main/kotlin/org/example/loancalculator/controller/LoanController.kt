package org.example.loancalculator.controller

import org.example.loancalculator.model.LoanRequest
import org.example.loancalculator.model.LoanResponse
import org.example.loancalculator.service.LoanCalculatorService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/loan")
class LoanController(private val loanCalculatorService: LoanCalculatorService) {

    @PostMapping("/calculate")
    fun calculateLoan(@RequestBody request: LoanRequest): LoanResponse {
        return loanCalculatorService.calculateLoan(request)
    }
}