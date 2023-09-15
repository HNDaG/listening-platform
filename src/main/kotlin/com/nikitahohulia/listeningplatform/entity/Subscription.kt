package com.nikitahohulia.listeningplatform.entity

import org.bson.codecs.pojo.annotations.BsonProperty
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "subscription")
data class Subscription(
    @Id
    val id: ObjectId? = null,
    @BsonProperty(value = "user_id")
    val userId: ObjectId,
    @BsonProperty(value = "publisher_id")
    val publisherId: ObjectId
)
