package com.nikitahohulia.listeningplatform.core.infrastructure.rest.advice

import com.mongodb.MongoException
import com.nikitahohulia.listeningplatform.core.application.exception.DuplicateException
import com.nikitahohulia.listeningplatform.core.application.exception.response.ExceptionResponse
import com.nikitahohulia.listeningplatform.core.application.exception.NotFoundException
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

    @ExceptionHandler(MongoException::class)
    fun serverError(ex: Exception): ResponseEntity<ExceptionResponse> {
        val errorResponse = ExceptionResponse(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            ex.message
        )
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse)
    }
}
