package com.nikitahohulia.listeningplatform.dto.request

import com.nikitahohulia.nats.commonmodels.user.User
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import org.bson.types.ObjectId

data class UserDtoRequest(
    val id: String?,
    @field:Size(min = 4, message = "Username cannot be less that 4 characters.")
    val username: String,
    @field:NotEmpty(message = "Email cannot be empty.")
    @field:Email(message = "Invalid email format. Please use a valid email address.")
    val email: String,
    @field:Pattern(
        regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).{8,}$",
        message = "Password must contain at least one uppercase letter, one lowercase letter, one digit," +
            " and be at least 8 characters long."
    )
    val password: String,
)

fun UserDtoRequest.toEntity() = com.nikitahohulia.listeningplatform.entity.User(
    id = id?.let { ObjectId(it) },
    username = username,
    email = email,
    password = password
)

fun UserDtoRequest.toProto(): User {
    val newBuilder = User.newBuilder()
    newBuilder
        .setEmail(email)
        .setPassword(password)
        .setUsername(username)
    if (id!=null) newBuilder.setId(id)
    return newBuilder.build()
}

fun User.toRequest() = UserDtoRequest(
        id = id.takeIf { hasId() },
        username = username,
        email = email,
        password = password
)
