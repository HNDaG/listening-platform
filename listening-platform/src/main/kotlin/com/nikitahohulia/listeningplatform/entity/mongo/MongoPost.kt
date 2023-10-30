package com.nikitahohulia.listeningplatform.entity.mongo

import com.nikitahohulia.listeningplatform.entity.Post
import com.nikitahohulia.listeningplatform.entity.mongo.MongoPost.Companion.COLLECTION_NAME
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = COLLECTION_NAME)
data class MongoPost(
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

fun Post.toMongo() = MongoPost(
    id = id,
    creatorId = creatorId,
    content = content,
    createdAt = createdAt,
    thumbsUp = thumbsUp
)

fun MongoPost.toEntity() = Post(
    id = this.id,
    creatorId = this.creatorId,
    content = this.content,
    createdAt = this.createdAt,
    thumbsUp = this.thumbsUp
)
