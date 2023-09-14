package com.nikitahohulia.listeningplatform.repository

import com.nikitahohulia.listeningplatform.entity.User
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Repository

@Repository
class CustomUserRepositoryImpl(private val mongoTemplate: MongoTemplate) : CustomUserRepository {

    val collectionName = "user"

    override fun getUserById(id: ObjectId): User? {
        val query = Query().addCriteria(Criteria.where("id").`is`(id))
        return mongoTemplate.findOne(query, User::class.java, collectionName)
    }

    override fun findAll(): List<User> {
        return mongoTemplate.findAll(User::class.java, collectionName)
    }

    override fun save(user: User): User? {
        return mongoTemplate.save(user, collectionName)
    }

    override fun deleteById(id: ObjectId) {
        val query = Query().addCriteria(Criteria.where("id").`is`(id))
        mongoTemplate.remove(query, User::class.java, collectionName)
    }

    override fun findByUsername(username: String): User? {
        val query = Query().addCriteria(Criteria.where("username").`is`(username))
        return mongoTemplate.findOne(query, User::class.java, collectionName)
    }

    override fun deleteUserByUsername(username: String) {
        val query = Query().addCriteria(Criteria.where("username").`is`(username))
        mongoTemplate.remove(query, User::class.java, collectionName)
    }
}
