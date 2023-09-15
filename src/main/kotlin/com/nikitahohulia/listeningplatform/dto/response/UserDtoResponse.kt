package com.nikitahohulia.listeningplatform.dto.response

import com.nikitahohulia.listeningplatform.entity.User

data class UserDtoResponse(
    val id: String,
    val username: String,
    val email: String,
    val subscriptions: List<SubscriptionDtoResponse> = mutableListOf(),
    val password: String,
    val publisherId: String?
)

fun User.toResponse() = UserDtoResponse(
    id = id?.toHexString() ?: "",
    username = username,
    email = email,
    subscriptions = subscriptions.map { it.toResponse() },
    password = password,
    publisherId = publisherId?.toHexString() ?: ""
)
