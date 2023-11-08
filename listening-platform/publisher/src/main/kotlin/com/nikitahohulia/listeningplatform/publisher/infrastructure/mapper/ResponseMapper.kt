package com.nikitahohulia.listeningplatform.publisher.infrastructure.mapper

import com.nikitahohulia.listeningplatform.publisher.domain.Publisher
import com.nikitahohulia.listeningplatform.publisher.infrastructure.dto.response.PublisherDtoResponse

fun Publisher.toResponse() = PublisherDtoResponse(
    id = id?.toHexString() ?: "",
    publisherName = publisherName,
    rating = rating
)
