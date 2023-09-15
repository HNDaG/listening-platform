package com.nikitahohulia.listeningplatform.dto.response

import com.nikitahohulia.listeningplatform.entity.Subscription

data class SubscriptionDtoResponse(
    val id: String,
    val userId: String,
    val publisherId: String
)

fun Subscription.toResponse() = SubscriptionDtoResponse(
    id = id?.toHexString() ?: "",
    userId = userId.toHexString(),
    publisherId = publisherId.toHexString()
)
