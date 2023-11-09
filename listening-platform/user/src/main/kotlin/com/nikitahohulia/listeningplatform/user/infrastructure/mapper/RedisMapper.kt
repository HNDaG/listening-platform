package com.nikitahohulia.listeningplatform.user.infrastructure.mapper

import com.nikitahohulia.listeningplatform.user.domain.User
import com.nikitahohulia.listeningplatform.user.infrastructure.repository.redis.entity.RedisUser

fun RedisUser.toEntity() = User(
    id = this.id,
    username = this.username,
    password = this.password,
    email = this.email,
    subscriptions = this.subscriptions,
    publisherId = this.publisherId
)

fun User.toRedisUser() = RedisUser(
    id = this.id,
    username = this.username,
    password = this.password,
    email = this.email,
    subscriptions = this.subscriptions,
    publisherId = this.publisherId
)
