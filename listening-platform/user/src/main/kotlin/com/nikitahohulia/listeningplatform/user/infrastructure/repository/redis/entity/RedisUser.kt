package com.nikitahohulia.listeningplatform.user.infrastructure.repository.redis.entity

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer
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
