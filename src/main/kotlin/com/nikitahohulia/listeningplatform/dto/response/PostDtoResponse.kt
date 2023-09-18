package com.nikitahohulia.listeningplatform.dto.response

import com.nikitahohulia.listeningplatform.entity.Post
import java.time.LocalDateTime

data class PostDtoResponse(
    val id: String,
    val creatorId: String,
    val content: String,
    val createdAt: LocalDateTime,
    val thumbsUp: Int,
)

fun Post.toResponse() = PostDtoResponse(
    id = id?.toHexString() ?: "",
    creatorId = creatorId.toHexString(),
    content = content,
    createdAt = createdAt,
    thumbsUp = thumbsUp,
)
