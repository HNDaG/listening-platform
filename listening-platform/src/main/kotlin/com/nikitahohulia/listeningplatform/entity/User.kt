package com.nikitahohulia.listeningplatform.entity

import org.bson.types.ObjectId

data class User(
    val id: ObjectId? = null,
    val username: String,
    val password: String,
    val email: String,
    val subscriptions: MutableSet<ObjectId> = mutableSetOf(),
    val publisherId: ObjectId? = null
)
