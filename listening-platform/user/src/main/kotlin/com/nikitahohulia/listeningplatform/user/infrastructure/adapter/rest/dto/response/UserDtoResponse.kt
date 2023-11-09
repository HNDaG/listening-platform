package com.nikitahohulia.listeningplatform.user.infrastructure.adapter.rest.dto.response

data class UserDtoResponse(
    val id: String,
    val username: String,
    val email: String,
    val subscriptions: List<String> = mutableListOf(),
    val password: String,
    val publisherId: String?
)
