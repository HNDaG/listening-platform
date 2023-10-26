package com.nikitahohulia.listeningplatform.entity.mongo

import com.nikitahohulia.listeningplatform.entity.Publisher
import com.nikitahohulia.listeningplatform.entity.mongo.MongoPublisher.Companion.COLLECTION_NAME
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

fun Publisher.toMongo() = MongoPublisher(
    id = this.id,
    publisherName = this.publisherName,
    password = this.password,
    rating = this.rating
)

fun MongoPublisher.toEntity() = Publisher(
    id = this.id,
    publisherName = this.publisherName,
    password = this.password,
    rating = this.rating
)
