package com.nikitahohulia.listeningplatform.post.infrastructure.dto.response

import java.time.LocalDateTime

data class PostDtoResponse(
    val id: String,
    val creatorId: String,
    val content: String,
    val createdAt: LocalDateTime,
    val thumbsUp: Int,
)
