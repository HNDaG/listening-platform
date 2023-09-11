package com.nikitahohulia.listeningplatform.exception

import jakarta.persistence.EntityNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ExceptionHandler {

    @ExceptionHandler(EntityNotFoundException::class)
    fun notFoundExceptionHandler(ex: Exception): ResponseEntity<ExceptionResponse> {
        val errorResponse = ExceptionResponse(
            HttpStatus.NOT_FOUND.value(),
            ex.message
        )
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse)
    }
}
