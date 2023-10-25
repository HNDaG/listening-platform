package com.nikitahohulia.listeningplatform.repository

import com.nikitahohulia.listeningplatform.entity.User
import org.bson.types.ObjectId
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface CustomUserRepository {
    fun findAll(): Flux<User>

    fun save(user: User): Mono<User>

    fun findByUsername(username: String): Mono<User>

    fun deleteUserByUsername(username: String): Mono<Long>

    fun findByPublisherId(publisherId: ObjectId): Mono<User>

    fun findPublisherIdsByUsername(username: String): Flux<ObjectId>
}
