package com.nikitahohulia.listeningplatform.post.domain

import org.bson.types.ObjectId
import java.time.LocalDateTime

data class Post(
    val id: ObjectId? = null,
    val creatorId: ObjectId,
    val content: String,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val thumbsUp: Int = 0
)
