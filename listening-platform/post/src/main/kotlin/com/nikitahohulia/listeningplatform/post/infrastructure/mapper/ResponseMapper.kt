package com.nikitahohulia.listeningplatform.post.infrastructure.mapper

import com.nikitahohulia.listeningplatform.post.domain.Post
import com.nikitahohulia.listeningplatform.post.infrastructure.dto.response.PostDtoResponse

fun Post.toResponse() = PostDtoResponse(
    id = id?.toHexString() ?: "",
    creatorId = creatorId.toHexString(),
    content = content,
    createdAt = createdAt,
    thumbsUp = thumbsUp,
)
