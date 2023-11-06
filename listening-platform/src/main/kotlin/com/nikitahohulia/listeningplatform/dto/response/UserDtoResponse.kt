package com.nikitahohulia.listeningplatform.dto.response

import com.nikitahohulia.listeningplatform.entity.User
import com.nikitahohulia.api.internal.v2.usersvc.commonmodels.user.User as ProtoUser

data class UserDtoResponse(
    val id: String,
    val username: String,
    val email: String,
    val subscriptions: List<String> = mutableListOf(),
    val password: String,
    val publisherId: String?
)

fun User.toResponse() = UserDtoResponse(
    id = id?.toHexString() ?: "",
    username = username,
    email = email,
    subscriptions = subscriptions.map { it.toHexString() },
    password = password,
    publisherId = publisherId?.toHexString() ?: ""
)

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
