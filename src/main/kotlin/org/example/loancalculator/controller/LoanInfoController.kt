package org.example.loancalculator.controller

import org.example.loancalculator.entity.LoanInfo
import org.example.loancalculator.service.LoanInfoService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/loaninfo")
class LoanInfoController(@Autowired private val loanInfoService: LoanInfoService) {

    @PostMapping
    fun createLoanInfo(@RequestBody loanInfo: LoanInfo): ResponseEntity<String> {
        val result = loanInfoService.createLoanInfo(loanInfo)
        return ResponseEntity.ok(result)
    }
}