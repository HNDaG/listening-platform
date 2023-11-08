package com.nikitahohulia.listeningplatform.user.infrastructure.rest

import com.nikitahohulia.listeningplatform.core.infrastructure.annotation.LogOnException
import com.nikitahohulia.listeningplatform.post.infrastructure.dto.response.PostDtoResponse
import com.nikitahohulia.listeningplatform.post.infrastructure.mapper.toResponse
import com.nikitahohulia.listeningplatform.publisher.infrastructure.dto.request.PublisherDtoRequest
import com.nikitahohulia.listeningplatform.publisher.infrastructure.dto.response.PublisherDtoResponse
import com.nikitahohulia.listeningplatform.publisher.infrastructure.mapper.toEntity
import com.nikitahohulia.listeningplatform.publisher.infrastructure.mapper.toResponse
import com.nikitahohulia.listeningplatform.user.application.service.UserServiceImpl
import com.nikitahohulia.listeningplatform.user.infrastructure.dto.request.UserDtoRequest
import com.nikitahohulia.listeningplatform.user.infrastructure.dto.response.UserDtoResponse
import com.nikitahohulia.listeningplatform.user.infrastructure.mapper.toEntity
import com.nikitahohulia.listeningplatform.user.infrastructure.mapper.toResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@LogOnException
@RestController
@RequestMapping("/api/V2/users")
class UserController(private val userService: UserServiceImpl) {

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    fun findAllUsers(): Flux<UserDtoResponse> {
        return userService.getAllUsers().map { it.toResponse() }
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{username}")
    fun findUserByUsername(@PathVariable username: String): Mono<UserDtoResponse> {
        return userService.getUserByUsername(username).map { it.toResponse() }
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    fun createUser(@Valid @RequestBody user: UserDtoRequest): Mono<UserDtoResponse> {
        return userService.createUser(user.toEntity()).map { it.toResponse() }
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{username}")
    fun deleteUser(@PathVariable username: String): Mono<Unit> {
        return userService.deleteUserByUsername(username)
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{username}/publisher/{publisherName}")
    fun deleteUsersPublisher(
        @PathVariable("username") username: String,
        @PathVariable("publisherName") publisherName: String
    ): Mono<Unit> {
        return userService.deleteUsersPublisher(username, publisherName)
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{username}/to/{publisherName}")
    fun subscribe(
        @PathVariable("username") username: String,
        @PathVariable("publisherName") publisherName: String
    ): Mono<Unit> {
        return userService.subscribe(username, publisherName)
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{username}/becomePublisher")
    fun becomePublisher(
        @PathVariable("username") username: String,
        @Valid @RequestBody publisherDtoRequest: PublisherDtoRequest
    ): Mono<PublisherDtoResponse> {
        return userService.becamePublisher(username, publisherDtoRequest.toEntity()).map { it.toResponse() }
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{username}/posts/{page}")
    fun getContentFromCreators(
        @PathVariable("username") username: String,
        @PathVariable page: Int
    ): Flux<PostDtoResponse> {
        return userService.getPostsFromFollowedCreators(username, page).map { it.toResponse() }
    }
}
