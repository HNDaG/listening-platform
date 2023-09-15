package com.nikitahohulia.listeningplatform.dto.request

import com.nikitahohulia.listeningplatform.entity.Post
import jakarta.validation.constraints.NotEmpty
import org.bson.types.ObjectId
import java.time.LocalDateTime

data class PostDtoRequest(
    val creatorId: String?,
    @field:NotEmpty(message = "Content cannot be empty.")
    val content: String,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val thumbsUp: Int = 0
)

fun PostDtoRequest.toEntity() = Post(
    creatorId = ObjectId(creatorId),
    content = content,
    createdAt = createdAt,
    thumbsUp = thumbsUp
)
