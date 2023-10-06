package com.nikitahohulia.listeningplatform.dto.request

import com.nikitahohulia.listeningplatform.entity.Publisher
import jakarta.validation.constraints.Size
import org.bson.types.ObjectId

data class PublisherDtoRequest(
    val id: String?,
    @field:Size(min = 4, max = 16, message = "Publisher name must be at between 4 and 16 characters.")
    val publisherName: String,
    @field:Size(min = 8, message = "Password is too short.")
    val password: String
)

fun PublisherDtoRequest.toEntity() = Publisher(
    id = id?.let { ObjectId(it) },
    publisherName = publisherName,
    password = password
)
