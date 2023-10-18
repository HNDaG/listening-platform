package com.nikitahohulia.listeningplatform.repository

import com.nikitahohulia.listeningplatform.entity.User
import com.nikitahohulia.listeningplatform.exception.NotFoundException
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
class CustomUserRepositoryImpl(private val mongoTemplate: ReactiveMongoTemplate) : CustomUserRepository {

    val collectionName = "user"

    override fun findById(id: ObjectId): Mono<User> {
        val query = Query().addCriteria(Criteria.where("id").`is`(id))
        return mongoTemplate.findOne(query, User::class.java, collectionName)
    }

    override fun findAll(): Flux<User> {
        return mongoTemplate.findAll(User::class.java, collectionName)
    }

    override fun save(user: User): Mono<User> {
        return mongoTemplate.save(user, collectionName)
    }

    override fun deleteById(id: ObjectId): Mono<Long> {
        val query = Query().addCriteria(Criteria.where("id").`is`(id))
        return mongoTemplate.remove(query, User::class.java, collectionName).map { it.deletedCount }
    }

    override fun findByUsername(username: String): Mono<User> {
        val query = Query().addCriteria(Criteria.where("username").`is`(username))
        return mongoTemplate.findOne(query, User::class.java, collectionName)
    }

    override fun deleteUserByUsername(username: String): Mono<Long> {
        val query = Query().addCriteria(Criteria.where("username").`is`(username))
        return mongoTemplate.remove(query, User::class.java, collectionName).map { it.deletedCount }
    }

    override fun findByPublisherId(publisherId: ObjectId): Mono<User> {
        val query = Query().addCriteria(Criteria.where("publisherId").`is`(publisherId))
        return mongoTemplate.findOne(query, User::class.java, collectionName)
    }

    override fun findPublisherIdsByUsername(username: String): Flux<ObjectId> {
        return findByUsername(username)
            .switchIfEmpty(Mono.error(NotFoundException("User not found with given username = $username")))
            .flatMapMany { user -> Flux.fromIterable(user.subscriptions) }
    }
}
