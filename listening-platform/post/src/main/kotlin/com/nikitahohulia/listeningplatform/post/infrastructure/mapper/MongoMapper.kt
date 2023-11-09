package com.nikitahohulia.listeningplatform.post.infrastructure.mapper

import com.nikitahohulia.listeningplatform.post.domain.Post
import com.nikitahohulia.listeningplatform.post.infrastructure.repository.mongo.entity.MongoPost

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
