package com.nikitahohulia.listeningplatform.user.infrastructure.adapter.redis

import com.nikitahohulia.listeningplatform.core.infrastructure.configuration.redis.CoreRedisConfiguration
import com.nikitahohulia.listeningplatform.user.infrastructure.repository.entity.RedisUser
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.core.ReactiveRedisTemplate

@Configuration
class RedisConfiguration : CoreRedisConfiguration<RedisUser>(clazz = RedisUser::class.java) {

    @Bean
    fun reactiveRedisTemplate(
        connectionFactory: ReactiveRedisConnectionFactory
    ): ReactiveRedisTemplate<String, RedisUser> = createReactiveRedisTemplate(clazz, connectionFactory)
}
