package com.nikitahohulia.listeningplatform.entity

import com.nikitahohulia.listeningplatform.entity.Post.Companion.COLLECTION_NAME
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = COLLECTION_NAME)
data class Post(
    @Id
    val id: ObjectId? = null,
    val creatorId: ObjectId,
    val content: String,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val thumbsUp: Int = 0
){
    companion object {
        const val COLLECTION_NAME = "post"
    }
}
