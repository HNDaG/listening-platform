package com.nikitahohulia.listeningplatform.dto.response

import com.nikitahohulia.listeningplatform.entity.Publisher

data class PublisherDtoResponse(
    val id: String,
    val publisherName: String,
    val rating: Double?
)

fun Publisher.toResponse() = PublisherDtoResponse(
    id = id?.toHexString() ?: "",
    publisherName = publisherName,
    rating = rating
)
