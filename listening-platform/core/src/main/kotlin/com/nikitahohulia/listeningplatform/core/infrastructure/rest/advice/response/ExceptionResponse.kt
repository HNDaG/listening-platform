package com.nikitahohulia.listeningplatform.core.infrastructure.rest.advice.response

import java.time.LocalDateTime

data class ExceptionResponse(
    val status: Int,
    val message: String?,
    val timestamp: LocalDateTime = LocalDateTime.now()
)
