package com.nikitahohulia.listeningplatform.dto.request

import com.nikitahohulia.listeningplatform.entity.User
import com.nikitahohulia.api.internal.v2.usersvc.commonmodels.user.User as NatsUser
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

fun UserDtoRequest.toEntity() = User(
    id = id?.let { ObjectId(it) },
    username = username,
    email = email,
    password = password
)

fun NatsUser.toRequest() = UserDtoRequest(
    id = id.takeIf { hasId() },
    username = username,
    email = email,
    password = password,
)

fun NatsUser.toEntity(): User {
    val user = User(
        id = ObjectId(id).takeIf { hasId() },
        username = username,
        subscriptions = subscriptionsList.map { ObjectId(it) }.toMutableSet(),
        email = email,
        password = password,
    )
    if (hasPublisherId())
        return user.copy(publisherId = ObjectId(publisherId))
    return user
}
