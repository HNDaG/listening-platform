package com.nikitahohulia.listeningplatform.user.application.port

import com.nikitahohulia.listeningplatform.user.domain.User
import reactor.core.publisher.Mono

interface UserCrudRepository {

    fun findByUsername(username: String): Mono<User>

    fun save(user: User): Mono<User>

    fun update(user: User): Mono<User>

    fun deleteByUsername(username: String): Mono<Unit>
}
