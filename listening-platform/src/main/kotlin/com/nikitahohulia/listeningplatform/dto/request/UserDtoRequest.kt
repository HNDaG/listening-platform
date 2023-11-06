package com.nikitahohulia.listeningplatform.dto.request

import com.nikitahohulia.listeningplatform.entity.User
import com.nikitahohulia.api.internal.v2.usersvc.commonmodels.user.User as ProtoUser
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
    val subscriptions: MutableSet<ObjectId> = mutableSetOf(),
    val publisherId: ObjectId? = null
)

fun UserDtoRequest.toEntity() = User(
    id = id?.let { ObjectId(it) },
    username = username,
    email = email,
    password = password,
    subscriptions = subscriptions,
    publisherId = publisherId
)

fun ProtoUser.toEntity(): User {
    return User(
        id = if (hasId()) ObjectId(id) else null,
        email = email,
        username = username,
        subscriptions = subscriptionsList.map { ObjectId(it) }.toMutableSet(),
        publisherId = if (hasPublisherId()) ObjectId(publisherId) else null,
        password = password
    )
}
