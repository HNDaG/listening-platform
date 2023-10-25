package com.nikitahohulia.listeningplatform.redis.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.nikitahohulia.listeningplatform.entity.User
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.StringRedisSerializer

@Configuration
class RedisConfig {


    @Bean
    fun reactiveRedisConnectionFactory(
        @Value("\${spring.data.redis.host}") host: String,
        @Value("\${spring.data.redis.port}") port: String,    
    ): ReactiveRedisConnectionFactory {
        val config = RedisStandaloneConfiguration(host, port.toInt())
        val factory = LettuceConnectionFactory(config)
        factory.afterPropertiesSet()
        return factory
    }

    @Bean
    fun reactiveRedisTemplate(
        @Qualifier("reactiveRedisConnectionFactory") connectionFactory: ReactiveRedisConnectionFactory
    ): ReactiveRedisTemplate<String, User> {
        val objectMapper = ObjectMapper().findAndRegisterModules()
        val serializer = Jackson2JsonRedisSerializer(objectMapper, User::class.java)
        val context = RedisSerializationContext
            .newSerializationContext<String, User>(StringRedisSerializer())
            .value(serializer)
            .build()
        return ReactiveRedisTemplate(connectionFactory, context)
    }

}
