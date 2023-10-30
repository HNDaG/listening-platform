package com.nikitahohulia.listeningplatform.entity.mongo

import com.nikitahohulia.listeningplatform.entity.User
import com.nikitahohulia.listeningplatform.entity.mongo.MongoUser.Companion.COLLECTION_NAME
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = COLLECTION_NAME)
data class MongoUser(
    @Id
    val id: ObjectId? = null,
    val username: String,
    val password: String,
    val email: String,
    val subscriptions: MutableSet<ObjectId> = mutableSetOf(),
    val publisherId: ObjectId? = null
){
    companion object {
        const val COLLECTION_NAME = "user"
    }
}

fun User.toMongo(): MongoUser {
    return MongoUser(
        id = this.id,
        username = this.username,
        password = this.password,
        email = this.email,
        subscriptions = this.subscriptions,
        publisherId = this.publisherId
    )
}

fun MongoUser.toEntity(): User {
    return User(
        id = this.id,
        username = this.username,
        password = this.password,
        email = this.email,
        subscriptions = this.subscriptions,
        publisherId = this.publisherId
    )
}
