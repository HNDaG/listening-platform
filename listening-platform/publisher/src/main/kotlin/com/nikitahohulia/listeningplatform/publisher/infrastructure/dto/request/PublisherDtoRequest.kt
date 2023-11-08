package com.nikitahohulia.listeningplatform.publisher.infrastructure.dto.request

import jakarta.validation.constraints.Size

data class PublisherDtoRequest(
    val id: String?,
    @field:Size(min = 4, max = 16, message = "Publisher name must be at between 4 and 16 characters.")
    val publisherName: String,
    @field:Size(min = 8, message = "Password is too short.")
    val password: String
)
