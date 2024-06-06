package org.example.loancalculator.result

import org.springframework.http.HttpStatus

data class Result<T>(
    val message: String,
    val status: HttpStatus,
    val data: T? = null
)

