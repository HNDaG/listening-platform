package com.nikitahohulia.listeningplatform.entity

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "post")
data class Post(
    @Id
    val id: ObjectId? = null,
    val creatorId: ObjectId,
    val content: String,
    val createdAt: LocalDateTime,
    val thumbsUp: Int
)
