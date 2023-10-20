package com.nikitahohulia.listeningplatform.entity

import com.nikitahohulia.listeningplatform.entity.User.Companion.COLLECTION_NAME
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = COLLECTION_NAME)
data class User(
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
