package com.nikitahohulia.listeningplatform.repository.cacheable

import com.nikitahohulia.listeningplatform.entity.User
import com.nikitahohulia.listeningplatform.repository.UserRepository
import com.nikitahohulia.listeningplatform.repository.redis.UserRedisRepository
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
class CacheableUserRepository (
    @Qualifier("mongoUserRepository") private val userRepository: UserRepository,
    private val redisUserRepository: UserRedisRepository
) : UserRepository {

    override fun findAll(): Flux<User> {
        return userRepository.findAll()
    }

    override fun save(user: User): Mono<User> {
        return userRepository.save(user).flatMap { redisUserRepository.save(it) }
    }

    override fun findByUsername(username: String): Mono<User> {
        return redisUserRepository.findByUsername(username)
            .switchIfEmpty(
                userRepository.findByUsername(username)
                    .flatMap { redisUserRepository.save(it) }
            )
    }

    override fun deleteUserByUsername(username: String): Mono<Long> {
        return redisUserRepository.deleteByUsername(username)
            .then(userRepository.deleteUserByUsername(username))
    }

    override fun findByPublisherId(publisherId: ObjectId): Mono<User> {
        return userRepository.findByPublisherId(publisherId)
            .flatMap { redisUserRepository.save(it) }
    }

    override fun findPublisherIdsByUsername(username: String): Flux<ObjectId> {
        return userRepository.findPublisherIdsByUsername(username)
    }
}
