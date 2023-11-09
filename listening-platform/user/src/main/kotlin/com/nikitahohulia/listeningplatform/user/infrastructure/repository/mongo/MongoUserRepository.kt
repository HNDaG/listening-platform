package com.nikitahohulia.listeningplatform.user.infrastructure.repository.mongo

import com.nikitahohulia.listeningplatform.core.application.exception.NotFoundException
import com.nikitahohulia.listeningplatform.user.application.port.UserRepositoryOutPort
import com.nikitahohulia.listeningplatform.user.domain.User
import com.nikitahohulia.listeningplatform.user.infrastructure.mapper.toEntity
import com.nikitahohulia.listeningplatform.user.infrastructure.mapper.toMongo
import com.nikitahohulia.listeningplatform.user.infrastructure.repository.mongo.entity.MongoUser
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
class MongoUserRepository(private val mongoTemplate: ReactiveMongoTemplate) : UserRepositoryOutPort {

    override fun findAll(): Flux<User> {
        return mongoTemplate.findAll<MongoUser>().map { it.toEntity() }
    }

    override fun save(user: User): Mono<User> {
        return mongoTemplate.save(user.toMongo()).map { it.toEntity() }
    }

    override fun findByUsername(username: String): Mono<User> {
        val query = Query().addCriteria(Criteria.where("username").`is`(username))
        return mongoTemplate.findOne<MongoUser>(query).map { it.toEntity() }
    }

    override fun deleteUserByUsername(username: String): Mono<Long> {
        val query = Query().addCriteria(Criteria.where("username").`is`(username))
        return mongoTemplate.remove<MongoUser>(query).map { it.deletedCount }
    }

    override fun findByPublisherId(publisherId: ObjectId): Mono<User> {
        val query = Query().addCriteria(Criteria.where("publisherId").`is`(publisherId))
        return mongoTemplate.findOne<MongoUser>(query).map { it.toEntity() }
    }

    override fun findPublisherIdsByUsername(username: String): Flux<ObjectId> {
        return findByUsername(username)
            .switchIfEmpty(Mono.error(NotFoundException("User not found with given username = $username")))
            .flatMapMany { user -> Flux.fromIterable(user.subscriptions) }
    }
}
