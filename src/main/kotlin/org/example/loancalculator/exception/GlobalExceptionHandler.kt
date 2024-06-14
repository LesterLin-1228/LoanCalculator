package org.example.loancalculator.exception

import jakarta.servlet.http.HttpServletRequest
import org.example.loancalculator.dto.error.ErrorResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.server.ResponseStatusException

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(ResponseStatusException::class)
    fun handleResponseStatusException(
        ex: ResponseStatusException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            status = ex.statusCode.value(),
            message = ex.reason,
            path = request.requestURI
        )
        return ResponseEntity.status(ex.statusCode).body(errorResponse)
    }
}