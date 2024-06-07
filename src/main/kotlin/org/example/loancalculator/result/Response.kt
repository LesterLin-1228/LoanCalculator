package org.example.loancalculator.result

import org.springframework.http.HttpStatus

data class Response<T>(
    val message: String,
    val status: HttpStatus,
    val data: T? = null
)

