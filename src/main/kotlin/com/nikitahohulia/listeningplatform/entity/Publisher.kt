package com.nikitahohulia.listeningplatform.entity

import org.bson.codecs.pojo.annotations.BsonProperty
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "publisher")
data class Publisher(
    @Id
    val id: ObjectId? = null,
    @BsonProperty(value = "publisher_name")
    val publisherName: String,
    val rating: Double? = null,
)
