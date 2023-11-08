package com.nikitahohulia.listeningplatform.user.infrastructure.repository.redis

import com.nikitahohulia.listeningplatform.user.application.port.UserCrudRepository
import com.nikitahohulia.listeningplatform.user.domain.User
import com.nikitahohulia.listeningplatform.user.infrastructure.mapper.toEntity
import com.nikitahohulia.listeningplatform.user.infrastructure.mapper.toRedisUser
import com.nikitahohulia.listeningplatform.user.infrastructure.repository.entity.RedisUser
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import java.time.Duration

@Repository
class UserRedisRepository(
    @Value("\${redis.ttl.minutes}") val redisTtlMinutes: String,
    @Value("\${redis.key.user-username-prefix}") val prefix: String,
    private val redisTemplate: ReactiveRedisTemplate<String, RedisUser>,
) : UserCrudRepository {
    override fun findByUsername(username: String): Mono<User> {
        return redisTemplate.opsForValue().get(prefix + username).map { it.toEntity() }
    }

    override fun save(user: User): Mono<User> {
        val redisUser = user.toRedisUser()
        return redisTemplate.opsForValue().set(
            prefix + redisUser.username,
            redisUser,
            Duration.ofMinutes(redisTtlMinutes.toLong())
        ).thenReturn(user)
    }

    override fun update(user: User): Mono<User> {
        return save(user)
    }

    override fun deleteByUsername(username: String): Mono<Unit> {
        return redisTemplate.delete(prefix + username).then(Unit.toMono())
    }
}
