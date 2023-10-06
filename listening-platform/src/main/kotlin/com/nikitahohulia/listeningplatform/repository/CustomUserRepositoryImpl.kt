package com.nikitahohulia.listeningplatform.repository

import com.nikitahohulia.listeningplatform.entity.User
import com.nikitahohulia.listeningplatform.exception.NotFoundException
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Repository

@Repository
class CustomUserRepositoryImpl(private val mongoTemplate: MongoTemplate) : CustomUserRepository {

    val collectionName = "user"

    override fun findUserById(id: ObjectId): User? {
        val query = Query().addCriteria(Criteria.where("id").`is`(id))
        return mongoTemplate.findOne(query, User::class.java, collectionName)
    }

    override fun findAll(): List<User> {
        return mongoTemplate.findAll(User::class.java, collectionName)
    }

    override fun save(user: User): User? {
        return mongoTemplate.save(user, collectionName)
    }

    override fun deleteById(id: ObjectId): Long {
        val query = Query().addCriteria(Criteria.where("id").`is`(id))
        val result = mongoTemplate.remove(query, User::class.java, collectionName)
        return result.deletedCount
    }

    override fun findByUsername(username: String): User? {
        val query = Query().addCriteria(Criteria.where("username").`is`(username))
        return mongoTemplate.findOne(query, User::class.java, collectionName)
    }

    override fun deleteUserByUsername(username: String): Long {
        val query = Query().addCriteria(Criteria.where("username").`is`(username))
        val result = mongoTemplate.remove(query, User::class.java, collectionName)
        return result.deletedCount
    }

    override fun findByPublisherId(publisherId: ObjectId): User? {
        val query = Query().addCriteria(Criteria.where("publisherId").`is`(publisherId))
        return mongoTemplate.findOne(query, User::class.java, collectionName)
    }

    override fun findPublisherIdsByUsername(username: String): List<ObjectId> {
        val user = findByUsername(username)
            ?: throw NotFoundException("User not found with given username = $username")
        return user.subscriptions.toList()
    }
}
