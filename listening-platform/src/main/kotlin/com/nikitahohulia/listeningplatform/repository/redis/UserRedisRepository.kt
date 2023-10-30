package com.nikitahohulia.listeningplatform.repository.redis

import com.nikitahohulia.listeningplatform.entity.User
import reactor.core.publisher.Mono

interface UserRedisRepository {

    fun findByUsername(username: String): Mono<User>

    fun save(user: User): Mono<User>

    fun update(user: User): Mono<User>

    fun deleteByUsername(username: String): Mono<Unit>
}
