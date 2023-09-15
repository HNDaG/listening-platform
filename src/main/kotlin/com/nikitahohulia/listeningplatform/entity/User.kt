package com.nikitahohulia.listeningplatform.entity

import org.bson.codecs.pojo.annotations.BsonProperty
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "user")
data class User(
    @Id
    val id: ObjectId? = null,
    val username: String,
    val password: String,
    val email: String,
    val subscriptions: List<Subscription> = mutableListOf(),
    @BsonProperty(value = "publisher_id")
    val publisher: Publisher? = null
)
