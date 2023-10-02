package com.nikitahohulia.listeningplatform.service

import com.nikitahohulia.listeningplatform.dto.request.PublisherDtoRequest
import com.nikitahohulia.listeningplatform.dto.request.UserDtoRequest
import com.nikitahohulia.listeningplatform.dto.request.toEntity
import com.nikitahohulia.listeningplatform.dto.response.PostDtoResponse
import com.nikitahohulia.listeningplatform.dto.response.PublisherDtoResponse
import com.nikitahohulia.listeningplatform.dto.response.UserDtoResponse
import com.nikitahohulia.listeningplatform.dto.response.toResponse
import com.nikitahohulia.listeningplatform.entity.User
import com.nikitahohulia.listeningplatform.exception.DuplicateException
import com.nikitahohulia.listeningplatform.exception.EntityNotFoundException
import com.nikitahohulia.listeningplatform.exception.NotFoundException
import com.nikitahohulia.listeningplatform.repository.CustomPostRepository
import com.nikitahohulia.listeningplatform.repository.CustomUserRepository
import com.nikitahohulia.listeningplatform.repository.PublisherRepository
import org.bson.types.ObjectId
import org.springframework.stereotype.Service

@Service
class UserServiceImpl(
    private val userRepository: CustomUserRepository,
    private val publisherRepository: PublisherRepository,
    private val postRepository: CustomPostRepository
) : UserService {

    override fun getUserByUsername(username: String): User {
        return userRepository.findByUsername(username)
            ?: throw NotFoundException("User not found with given username = $username")
    }

    override fun getUserById(id: String): User {
        return userRepository.findUserById(ObjectId(id))
            ?: throw NotFoundException("User not found with given id = $id")
    }

    override fun createUser(userDtoRequest: UserDtoRequest): UserDtoResponse {
        val existingUser = userRepository.findByUsername(userDtoRequest.username)
        if (existingUser != null) {
            throw DuplicateException("Username already exists")
        }
        val newUser = userRepository.save(userDtoRequest.toEntity())
        return newUser?.toResponse() ?: throw NotFoundException("Internal error, failed to save new user")
    }

    override fun becamePublisher(username: String, publisherDtoRequest: PublisherDtoRequest): PublisherDtoResponse {
        val existingPublisher = publisherRepository.findByPublisherName(publisherDtoRequest.publisherName)
        if (existingPublisher != null) {
            throw DuplicateException("Publisher with the same PublisherName already exists")
        }
        val newPublisher = publisherRepository.save(publisherDtoRequest.toEntity())
        val user = userRepository.findByUsername(username)
        if (user != null) {
            userRepository.save(user.copy(publisherId = newPublisher.id))
            return newPublisher.toResponse()
        }
        throw IllegalArgumentException("Database internal fail")
    }

    override fun updateUser(id: String, user: User): UserDtoResponse {
        val existingUser = userRepository.findUserById(ObjectId(id))
        existingUser.let {
            val updatedUser = user.copy(id = ObjectId(id))
            userRepository.save(updatedUser)
            return updatedUser.toResponse()
        }
    }

    override fun getAllUsers(): List<UserDtoResponse> {
        return userRepository.findAll().map { it.toResponse() }
    }

    override fun deleteUserById(id: String) {
        if (userRepository.deleteById(ObjectId(id)) == 0L)
            throw EntityNotFoundException("User with ID - $id not found")
        return
    }

    override fun deleteUserByUsername(username: String) {
        if (userRepository.deleteUserByUsername(username) == 0L)
            throw EntityNotFoundException("User with username - $username not found")
        return
    }

    override fun subscribe(username: String, publisherName: String) {
        val user = getUserByUsername(username)
        val publisher = publisherRepository.findByPublisherName(publisherName)
            ?: throw NotFoundException("Publisher not found with given publisherName = $publisherName")
        if (user.id != null && publisher.id != null) {
            user.subscriptions.add(publisher.id)
            userRepository.save(user.copy(id = user.id))
            return
        }
        throw IllegalArgumentException("Database internal fail")
    }

    override fun getPostsFromFollowedCreators(username: String, page: Int): List<PostDtoResponse> {
        val ids = userRepository.findPublisherIdsByUsername(username)
        return postRepository.findAllBySubscriptionIds(ids, page, DEFAULT_PAGE_SIZE).map { it.toResponse() }
    }

    private companion object {
        const val DEFAULT_PAGE_SIZE: Int = 5
    }
}
