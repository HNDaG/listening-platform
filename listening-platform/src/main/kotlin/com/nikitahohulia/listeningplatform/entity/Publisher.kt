package com.nikitahohulia.listeningplatform.entity

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "publisher")
data class Publisher(
    @Id
    val id: ObjectId? = null,
    val publisherName: String,
    val password: String,
    val rating: Double? = null,
)
