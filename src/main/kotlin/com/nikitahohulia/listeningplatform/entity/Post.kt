package com.nikitahohulia.listeningplatform.entity

import org.bson.codecs.pojo.annotations.BsonProperty
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "post")
data class Post(
    @Id
    val id: ObjectId? = null,
    @BsonProperty(value = "creator_id")
    val creatorId: ObjectId,
    val content: String,
    @BsonProperty(value = "created_at")
    val createdAt: LocalDateTime,
    @BsonProperty(value = "thumbs_up")
    val thumbsUp: Int
)
