package com.nikitahohulia.listeningplatform.redis.repositiry

import com.nikitahohulia.listeningplatform.entity.User
import org.bson.types.ObjectId
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono


interface UserRedisRepository {

    fun findByUsername(username: String): Mono<User>

    fun save(user: User): Mono<User>

    fun update(user: User): Mono<User>

    fun deleteByUsername(username: String): Mono<Unit>
}
