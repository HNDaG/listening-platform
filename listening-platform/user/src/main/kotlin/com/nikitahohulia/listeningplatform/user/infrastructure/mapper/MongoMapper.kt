package com.nikitahohulia.listeningplatform.user.infrastructure.mapper

import com.nikitahohulia.listeningplatform.user.domain.User
import com.nikitahohulia.listeningplatform.user.infrastructure.repository.mongo.entity.MongoUser

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
