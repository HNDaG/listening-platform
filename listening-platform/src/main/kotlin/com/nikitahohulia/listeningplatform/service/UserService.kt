package com.nikitahohulia.listeningplatform.service

import com.nikitahohulia.listeningplatform.dto.request.PublisherDtoRequest
import com.nikitahohulia.listeningplatform.dto.request.UserDtoRequest
import com.nikitahohulia.listeningplatform.dto.response.PostDtoResponse
import com.nikitahohulia.listeningplatform.dto.response.PublisherDtoResponse
import com.nikitahohulia.listeningplatform.dto.response.UserDtoResponse
import com.nikitahohulia.listeningplatform.entity.User

interface UserService {

    fun createUser(userDtoRequest: UserDtoRequest): UserDtoResponse

    fun becamePublisher(username: String, publisherDtoRequest: PublisherDtoRequest): PublisherDtoResponse

    fun updateUser(id: String, user: User): UserDtoResponse

    fun getUserByUsername(username: String): User

    fun getUserById(id: String): User

    fun getAllUsers(): List<UserDtoResponse>

    fun deleteUserById(id: String)

    fun deleteUserByUsername(username: String)

    fun subscribe(username: String, publisherName: String)

    fun getPostsFromFollowedCreators(username: String, page: Int, pageSize: Int): List<PostDtoResponse>
}
