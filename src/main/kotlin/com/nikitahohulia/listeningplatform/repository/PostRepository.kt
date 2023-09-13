package com.nikitahohulia.listeningplatform.repository

import com.nikitahohulia.listeningplatform.entity.Post
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface PostRepository : MongoRepository<Post, ObjectId> {

    fun getPostsByCreatorIdOrderByCreatedAt(creatorId: ObjectId): List<Post>
}
