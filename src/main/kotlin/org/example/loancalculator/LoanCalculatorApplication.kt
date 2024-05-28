package org.example.loancalculator

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class LoanCalculatorApplication

fun main(args: Array<String>) {
    runApplication<LoanCalculatorApplication>(*args)
}
