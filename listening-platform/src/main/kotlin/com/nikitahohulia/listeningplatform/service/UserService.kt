package com.nikitahohulia.listeningplatform.service

import com.nikitahohulia.listeningplatform.entity.Post
import com.nikitahohulia.listeningplatform.entity.Publisher
import com.nikitahohulia.listeningplatform.entity.User
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface UserService {

    fun createUser(user: User): Mono<User>

    fun becamePublisher(username: String, publisher: Publisher): Mono<Publisher>

    fun updateUser(oldUsername: String, user: User): Mono<User>

    fun getUserByUsername(username: String): Mono<User>

    fun getAllUsers(): Flux<User>

    fun deleteUserByUsername(username: String): Mono<Unit>

    fun subscribe(username: String, publisherName: String): Mono<Unit>

    fun getPostsFromFollowedCreators(username: String, page: Int): Flux<Post>
}
