package org.example.loancalculator.controller

import org.example.loancalculator.entity.Loan
import org.example.loancalculator.service.LoanService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/loan")
class LoanController(@Autowired private val loanService: LoanService) {

    @PostMapping
    fun createLoan(@RequestBody loan: Loan): ResponseEntity<String> {
        val result = loanService.createLoan(loan)
        return ResponseEntity.ok(result)
    }
}