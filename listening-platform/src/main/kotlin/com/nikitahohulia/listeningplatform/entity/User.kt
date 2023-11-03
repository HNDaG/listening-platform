package com.nikitahohulia.listeningplatform.entity

import org.bson.types.ObjectId
import com.nikitahohulia.api.internal.v2.usersvc.commonmodels.user.User as ProtoUser

data class User(
    val id: ObjectId? = null,
    val username: String,
    val password: String,
    val email: String,
    val subscriptions: MutableSet<ObjectId> = mutableSetOf(),
    val publisherId: ObjectId? = null
)

fun User.toProto(): ProtoUser {
    val builder = com.nikitahohulia.api.internal.v2.usersvc.commonmodels.user.User.newBuilder()
        .setEmail(email)
        .setPassword(password)
        .setUsername(username)

    if (id!=null) builder.setId(id.toHexString())
    if (publisherId!=null) builder.setPublisherId(publisherId.toHexString())
    subscriptions.forEach { builder.addSubscriptions(it.toHexString()) }
    return builder.build()
}
