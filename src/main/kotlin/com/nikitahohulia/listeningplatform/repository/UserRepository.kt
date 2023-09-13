package com.nikitahohulia.listeningplatform.repository

import com.nikitahohulia.listeningplatform.entity.User
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : MongoRepository<User, ObjectId> {

    fun findByUsername(username: String): User?

    fun deleteUserByUsername(username: String)
}
