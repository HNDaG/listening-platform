package com.nikitahohulia.listeningplatform.publisher.domain

import org.bson.types.ObjectId

data class Publisher(
    val id: ObjectId? = null,
    val publisherName: String,
    val password: String,
    val rating: Double? = null,
)
