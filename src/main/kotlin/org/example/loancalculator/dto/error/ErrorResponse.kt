package org.example.loancalculator.dto.error

data class ErrorResponse(
    val status: Int,
    val message: String?,
    val path: String
)
