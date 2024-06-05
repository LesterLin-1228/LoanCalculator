package org.example.loancalculator.controller

import jakarta.validation.Valid
import org.example.loancalculator.dto.loanDto
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
    fun createLoan(@RequestBody @Valid loanDto: loanDto): ResponseEntity<String> {
        val result = loanService.createLoan(loanDto)
        return ResponseEntity(result.message, result.status)
    }
}