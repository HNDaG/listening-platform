package com.nikitahohulia.listeningplatform.repository

import com.nikitahohulia.listeningplatform.entity.User
import org.bson.types.ObjectId

interface CustomUserRepository {

    fun findUserById(id: ObjectId): User?

    fun findAll(): List<User>

    fun save(user: User): User?

    fun deleteById(id: ObjectId)

    fun findByUsername(username: String): User?

    fun deleteUserByUsername(username: String)

    fun findByPublisherId(publisherId: ObjectId): User?

    fun findPublisherIdsByUsername(username: String): List<ObjectId>
}
