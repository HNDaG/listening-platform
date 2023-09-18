package com.nikitahohulia.listeningplatform.entity

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "subscription")
data class Subscription(
    @Id
    val id: ObjectId? = null,
    val userId: ObjectId,
    val publisherId: ObjectId
)
