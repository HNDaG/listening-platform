package com.nikitahohulia.listeningplatform.core.infrastructure.configuration.redis

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.StringRedisSerializer


open class CoreRedisConfiguration<T>(
    val clazz: Class<T>
) {
    protected fun <T> createReactiveRedisTemplate(
        clazz: Class<T>,
        connectionFactory: ReactiveRedisConnectionFactory
    ): ReactiveRedisTemplate<String, T> {
        val objectMapper = ObjectMapper().findAndRegisterModules()
        val serializer = Jackson2JsonRedisSerializer(objectMapper, clazz)
        val context = RedisSerializationContext
            .newSerializationContext<String, T>(StringRedisSerializer())
            .value(serializer)
            .build()
        return ReactiveRedisTemplate(connectionFactory, context)
    }
}
