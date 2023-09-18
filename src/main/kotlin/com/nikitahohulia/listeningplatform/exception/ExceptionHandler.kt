package com.nikitahohulia.listeningplatform.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.lang.IllegalArgumentException

@RestControllerAdvice
class ExceptionHandler {

    @ExceptionHandler(NotFoundException::class)
    fun notFoundExceptionHandler(ex: Exception): ResponseEntity<ExceptionResponse> {
        val errorResponse = ExceptionResponse(
            HttpStatus.NOT_FOUND.value(),
            ex.message
        )
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse)
    }

    @ExceptionHandler(DuplicateException::class)
    fun duplicateExceptionHandler(ex: Exception): ResponseEntity<ExceptionResponse> {
        val errorResponse = ExceptionResponse(
            HttpStatus.BAD_REQUEST.value(),
            ex.message
        )
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class, IllegalArgumentException::class)
    fun notValidArgumentHandler(ex: Exception): ResponseEntity<ExceptionResponse> {
        val errorResponse = ExceptionResponse(
            HttpStatus.BAD_REQUEST.value(),
            ex.message
        )
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
    }
}
