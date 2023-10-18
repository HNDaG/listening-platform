package com.nikitahohulia.listeningplatform.service

import com.nikitahohulia.listeningplatform.dto.request.PublisherDtoRequest
import com.nikitahohulia.listeningplatform.dto.request.UserDtoRequest
import com.nikitahohulia.listeningplatform.dto.response.PostDtoResponse
import com.nikitahohulia.listeningplatform.dto.response.PublisherDtoResponse
import com.nikitahohulia.listeningplatform.dto.response.UserDtoResponse
import com.nikitahohulia.listeningplatform.entity.User
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface UserService {

    fun createUser(userDtoRequest: UserDtoRequest): Mono<UserDtoResponse>

    fun becamePublisher(username: String, publisherDtoRequest: PublisherDtoRequest): Mono<PublisherDtoResponse>

    fun updateUser(id: String, user: User): Mono<UserDtoResponse>

    fun getUserByUsername(username: String): Mono<UserDtoResponse>

    fun getUserById(id: String): Mono<UserDtoResponse>

    fun getAllUsers(): Flux<UserDtoResponse>

    fun deleteUserById(id: String): Mono<Unit>

    fun deleteUserByUsername(username: String): Mono<Unit>

    fun subscribe(username: String, publisherName: String): Mono<Unit>

    fun getPostsFromFollowedCreators(username: String, page: Int): Flux<PostDtoResponse>
}
