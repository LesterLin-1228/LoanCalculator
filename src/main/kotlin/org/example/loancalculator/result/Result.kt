package org.example.loancalculator.result

import org.springframework.http.HttpStatus

data class Result(
    val message: String,
    val status: HttpStatus,
)

