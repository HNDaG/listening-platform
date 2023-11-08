package com.nikitahohulia.listeningplatform.post.infrastructure.dto.request

import jakarta.validation.constraints.NotEmpty
import java.time.LocalDateTime

data class PostDtoRequest(
    val creatorId: String?,
    @field:NotEmpty(message = "Content cannot be empty.")
    val content: String,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val thumbsUp: Int = 0
)
