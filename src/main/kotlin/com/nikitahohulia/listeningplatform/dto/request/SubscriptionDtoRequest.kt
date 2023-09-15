package com.nikitahohulia.listeningplatform.dto.request

import com.nikitahohulia.listeningplatform.entity.Subscription
import jakarta.validation.constraints.NotNull
import org.bson.types.ObjectId

data class SubscriptionDtoRequest(
    val id: String?,
    @NotNull
    val userId: String,
    @NotNull
    val publisherId: String
)

fun SubscriptionDtoRequest.toEntity() = Subscription(
    id = id?.let { ObjectId(it) },
    userId = ObjectId(userId),
    publisherId = ObjectId(publisherId)
)
