package com.nikitahohulia.listeningplatform.repository

import com.nikitahohulia.listeningplatform.entity.Post
import org.bson.types.ObjectId

interface CustomPostRepository {

    fun getPostById(id: ObjectId): Post?

    fun findAll(): List<Post>

    fun findAllByCreatorId(id: ObjectId): List<Post>

    fun save(post: Post): Post?

    fun deleteById(id: ObjectId)

    fun deleteByPublisherId(id: ObjectId)

    fun getPostsByCreatorIdOrderByCreatedAt(creatorId: ObjectId): List<Post>
}
