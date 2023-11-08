package com.nikitahohulia.listeningplatform.publisher.infrastructure.mapper

import com.nikitahohulia.listeningplatform.publisher.domain.Publisher
import com.nikitahohulia.listeningplatform.publisher.infrastructure.dto.request.PublisherDtoRequest
import org.bson.types.ObjectId

fun PublisherDtoRequest.toEntity() = Publisher(
    id = id?.let { ObjectId(it) },
    publisherName = publisherName,
    password = password
)
