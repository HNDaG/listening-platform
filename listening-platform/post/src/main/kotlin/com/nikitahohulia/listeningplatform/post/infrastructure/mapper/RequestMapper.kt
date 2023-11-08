package com.nikitahohulia.listeningplatform.post.infrastructure.mapper

import com.nikitahohulia.listeningplatform.post.domain.Post
import com.nikitahohulia.listeningplatform.post.infrastructure.dto.request.PostDtoRequest
import org.bson.types.ObjectId

fun PostDtoRequest.toEntity() = Post(
    creatorId = ObjectId(creatorId),
    content = content,
    createdAt = createdAt,
    thumbsUp = thumbsUp
)
