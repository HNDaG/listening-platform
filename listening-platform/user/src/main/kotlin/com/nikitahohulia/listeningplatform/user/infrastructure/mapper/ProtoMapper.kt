package com.nikitahohulia.listeningplatform.user.infrastructure.mapper

import com.nikitahohulia.listeningplatform.user.domain.User
import com.nikitahohulia.api.internal.v2.usersvc.commonmodels.user.User as ProtoUser
import com.nikitahohulia.listeningplatform.user.infrastructure.adapter.rest.dto.response.UserDtoResponse
import org.bson.types.ObjectId

fun ProtoUser.toEntity(): User {
    return User(
        id = if (hasId()) ObjectId(id) else null,
        email = email,
        username = username,
        subscriptions = subscriptionsList.map { ObjectId(it) }.toMutableSet(),
        publisherId = if (hasPublisherId()) ObjectId(publisherId) else null,
        password = password
    )
}

fun UserDtoResponse.toProto(): ProtoUser {
    val builder = ProtoUser.newBuilder()
        .setId(id)
        .setEmail(email)
        .setPassword(password)
        .setUsername(username)

    if (!publisherId.isNullOrEmpty()) builder.setPublisherId(publisherId)
    subscriptions.forEach { builder.addSubscriptions(it) }

    return builder.build()
}

fun User.toProto(): ProtoUser {
    return ProtoUser.newBuilder().also{ protoUser ->
        id?.let { protoUser.id = it.toHexString() }
        publisherId?.let { protoUser.publisherId = publisherId.toHexString() }

        protoUser.email = email
        protoUser.password = password
        protoUser.username = username

        protoUser.addAllSubscriptions(
            subscriptions.map { it.toHexString() }
        )
    }.build()
}
