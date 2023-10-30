package com.nikitahohulia.listeningplatform.entity

import org.bson.types.ObjectId

data class Publisher(
    val id: ObjectId? = null,
    val publisherName: String,
    val password: String,
    val rating: Double? = null,
)
