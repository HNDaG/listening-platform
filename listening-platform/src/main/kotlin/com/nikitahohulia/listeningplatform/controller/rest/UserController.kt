package com.nikitahohulia.listeningplatform.controller.rest

import com.nikitahohulia.listeningplatform.bpp.annotation.LogOnException
import com.nikitahohulia.listeningplatform.dto.request.PublisherDtoRequest
import com.nikitahohulia.listeningplatform.dto.request.UserDtoRequest
import com.nikitahohulia.listeningplatform.dto.response.PostDtoResponse
import com.nikitahohulia.listeningplatform.dto.response.PublisherDtoResponse
import com.nikitahohulia.listeningplatform.dto.response.UserDtoResponse
import com.nikitahohulia.listeningplatform.service.UserServiceImpl
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
@RequestMapping("/api/V1/users")
class UserController(private val userService: UserServiceImpl) {

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    fun findAllUsers(): Flux<UserDtoResponse> {
        return userService.getAllUsers()
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{username}")
    fun findUserByUsername(@PathVariable username: String): Mono<UserDtoResponse> {
        return userService.getUserByUsername(username)
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    fun createUser(@Valid @RequestBody user: UserDtoRequest): Mono<UserDtoResponse> {
        return userService.createUser(user)
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{username}")
    fun deleteUser(@PathVariable username: String): Mono<Unit> {
        return userService.deleteUserByUsername(username)
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
        return userService.becamePublisher(username, publisherDtoRequest)
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{username}/posts/{page}")
    fun getContentFromCreators(
        @PathVariable("username") username: String,
        @PathVariable page: Int
    ): Flux<PostDtoResponse> {
        return userService.getPostsFromFollowedCreators(username, page)
    }
}
