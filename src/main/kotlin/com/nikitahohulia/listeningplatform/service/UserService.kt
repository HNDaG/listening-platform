package com.nikitahohulia.listeningplatform.service

import com.nikitahohulia.listeningplatform.entity.User

interface UserService {

    fun createUser(user: User): User

    fun getUserById(id: Long): User

    fun getAllUsers(): List<User>

    fun deleteUser(id: Long)
}
