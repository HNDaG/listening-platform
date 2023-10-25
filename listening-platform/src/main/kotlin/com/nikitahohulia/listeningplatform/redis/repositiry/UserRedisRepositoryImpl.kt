package com.nikitahohulia.listeningplatform.redis.repositiry

import com.nikitahohulia.listeningplatform.entity.User
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.core.ScanOptions
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import java.time.Duration

@Repository
class UserRedisRepositoryImpl(
    private val redisTemplate: ReactiveRedisTemplate<String, User>
) : UserRedisRepository {

    @Value("\${redis.ttl.minutes}")
    private lateinit var redisTtlMinutes: String

    @Value("\${redis.key.user-username-prefix}")
    private lateinit var prefix: String


    override fun findByUsername(username: String): Mono<User> {
        return redisTemplate.opsForValue().get(prefix + username)
    }

    override fun save(user: User): Mono<User> {
        val key = user.username
        return user.id?.let {
            redisTemplate.opsForValue().set(
                prefix + key, user, Duration.ofMinutes(redisTtlMinutes.toLong())
            ).thenReturn(user)
        } ?: Mono.empty()
    }

    override fun update(user: User): Mono<User> {
        return save(user)
    }

    override fun deleteByUsername(username: String): Mono<Unit> {
        return redisTemplate.delete(prefix + username).then(Unit.toMono())
    }
}
