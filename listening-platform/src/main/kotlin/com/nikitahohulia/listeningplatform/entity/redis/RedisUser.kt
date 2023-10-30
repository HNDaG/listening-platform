package com.nikitahohulia.listeningplatform.entity.redis

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer
import com.nikitahohulia.listeningplatform.entity.User
import org.bson.types.ObjectId

data class RedisUser(
    @JsonSerialize(using = ToStringSerializer::class)
    val id: ObjectId? = null,
    val username: String,
    val password: String,
    val email: String,
    @JsonSerialize(contentUsing = ToStringSerializer::class)
    val subscriptions: MutableSet<ObjectId> = mutableSetOf(),
    @JsonSerialize(using = ToStringSerializer::class)
    val publisherId: ObjectId? = null
)

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
