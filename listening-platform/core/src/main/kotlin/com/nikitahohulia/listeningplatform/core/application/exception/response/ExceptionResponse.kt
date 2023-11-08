package com.nikitahohulia.listeningplatform.core.application.exception.response

import java.time.LocalDateTime

data class ExceptionResponse(
    val status: Int,
    val message: String?,
    val timestamp: LocalDateTime = LocalDateTime.now()
)
