package com.nikitahohulia.listeningplatform.user.infrastructure.mapper

import com.nikitahohulia.listeningplatform.user.domain.User
import com.nikitahohulia.listeningplatform.user.infrastructure.adapter.rest.dto.request.UserDtoRequest
import org.bson.types.ObjectId

fun UserDtoRequest.toEntity() = User(
    id = id?.let { ObjectId(it) },
    username = username,
    email = email,
    password = password,
    subscriptions = subscriptions,
    publisherId = publisherId
)
