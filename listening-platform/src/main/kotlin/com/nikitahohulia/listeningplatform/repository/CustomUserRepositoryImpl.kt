package com.nikitahohulia.listeningplatform.repository

import com.nikitahohulia.listeningplatform.entity.User
import com.nikitahohulia.listeningplatform.exception.NotFoundException
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.findAll
import org.springframework.data.mongodb.core.findOne
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.remove
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
class CustomUserRepositoryImpl(private val mongoTemplate: ReactiveMongoTemplate) : CustomUserRepository {

    override fun findAll(): Flux<User> {
        return mongoTemplate.findAll<User>()
    }

    override fun save(user: User): Mono<User> {
        return mongoTemplate.save(user)
    }

    override fun findByUsername(username: String): Mono<User> {
        val query = Query().addCriteria(Criteria.where("username").`is`(username))
        return mongoTemplate.findOne<User>(query)
    }

    override fun deleteUserByUsername(username: String): Mono<Long> {
        val query = Query().addCriteria(Criteria.where("username").`is`(username))
        return mongoTemplate.remove<User>(query).map { it.deletedCount }
    }

    override fun findByPublisherId(publisherId: ObjectId): Mono<User> {
        val query = Query().addCriteria(Criteria.where("publisherId").`is`(publisherId))
        return mongoTemplate.findOne<User>(query)
    }

    override fun findPublisherIdsByUsername(username: String): Flux<ObjectId> {
        return findByUsername(username)
            .switchIfEmpty(Mono.error(NotFoundException("User not found with given username = $username")))
            .flatMapMany { user -> Flux.fromIterable(user.subscriptions) }
    }
}
