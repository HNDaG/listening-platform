package com.nikitahohulia.listeningplatform.exception

import java.time.LocalDateTime

data class ExceptionResponse(
    val status: Int,
    val message: String?,
    val timestamp: LocalDateTime = LocalDateTime.now()
)
