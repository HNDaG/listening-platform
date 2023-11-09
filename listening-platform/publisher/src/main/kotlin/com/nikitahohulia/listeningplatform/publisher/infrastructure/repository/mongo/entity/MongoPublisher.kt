package com.nikitahohulia.listeningplatform.publisher.infrastructure.repository.mongo.entity

import com.nikitahohulia.listeningplatform.publisher.infrastructure.repository.mongo.entity.MongoPublisher.Companion.COLLECTION_NAME
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = COLLECTION_NAME)
data class MongoPublisher(
    @Id
    val id: ObjectId? = null,
    val publisherName: String,
    val password: String,
    val rating: Double? = null,
){
    companion object {
        const val COLLECTION_NAME = "publisher"
    }
}
