package com.nikitahohulia.listeningplatform.service

import com.nikitahohulia.listeningplatform.entity.User
import com.nikitahohulia.listeningplatform.repository.UserRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class UserServiceImpl(private val userRepository: UserRepository): UserService {
    override fun getUserById(id: Long): User? {
        return userRepository.findByIdOrNull(id)
    }

    override fun createUser(user: User): User {
        return userRepository.save(user)
    }

    override fun getAllUsers(): List<User> {
        return userRepository.findAll()
    }

    override fun deleteUser(id: Long) {
        return userRepository.deleteById(id)
    }
}
