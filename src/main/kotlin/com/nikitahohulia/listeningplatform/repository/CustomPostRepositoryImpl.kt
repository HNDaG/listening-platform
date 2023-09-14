package com.nikitahohulia.listeningplatform.repository

import com.nikitahohulia.listeningplatform.entity.Post
import org.bson.types.ObjectId
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Repository

@Repository
class CustomPostRepositoryImpl(private val mongoTemplate: MongoTemplate) : CustomPostRepository {

    val collectionName = "post"

    override fun getPostById(id: ObjectId): Post? {
        val query = Query().addCriteria(Criteria.where("id").`is`(id))
        return mongoTemplate.findOne(query, Post::class.java, collectionName)
    }

    override fun findAll(): List<Post> {
        return mongoTemplate.findAll(Post::class.java, collectionName)
    }

    override fun findByCreatorId(id: ObjectId): List<Post> {
        val query = Query().addCriteria(Criteria.where("creatorId").`is`(id))
        println(id.toHexString())
        return mongoTemplate.find(query, Post::class.java, collectionName)
    }

    override fun save(post: Post): Post? {
        return mongoTemplate.save(post, collectionName)
    }

    override fun deleteById(id: ObjectId) {
        val query = Query().addCriteria(Criteria.where("id").`is`(id))
        mongoTemplate.remove(query, Post::class.java)
    }

    override fun deleteByPublisherId(id: ObjectId) {
        val query = Query().addCriteria(Criteria.where("creatorId").`is`(id))
        mongoTemplate.remove(query, Post::class.java)
    }

    override fun getPostsByCreatorIdOrderByCreatedAt(creatorId: ObjectId): List<Post> {
        val query = Query().addCriteria(Criteria.where("creatorId").`is`(creatorId))
        query.with(Sort.by(Sort.Order.desc("createdAt")))
        return mongoTemplate.find(query, Post::class.java, collectionName)
    }
}
