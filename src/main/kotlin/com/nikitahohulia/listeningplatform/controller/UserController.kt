package com.nikitahohulia.listeningplatform.controller

import com.nikitahohulia.listeningplatform.bpp.LogOnException
import com.nikitahohulia.listeningplatform.dto.request.PublisherDtoRequest
import com.nikitahohulia.listeningplatform.dto.request.UserDtoRequest
import com.nikitahohulia.listeningplatform.dto.response.PostDtoResponse
import com.nikitahohulia.listeningplatform.dto.response.PublisherDtoResponse
import com.nikitahohulia.listeningplatform.dto.response.SubscriptionDtoResponse
import com.nikitahohulia.listeningplatform.dto.response.UserDtoResponse
import com.nikitahohulia.listeningplatform.entity.User
import com.nikitahohulia.listeningplatform.service.UserServiceImpl
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@LogOnException
@RestController
@RequestMapping("/api/V1/users")
class UserController(private val userService: UserServiceImpl) {

    @GetMapping
    fun findAllUsers(): ResponseEntity<List<UserDtoResponse>> {
        val users = userService.getAllUsers()
        return ResponseEntity.ok(users)
    }

    @GetMapping("/{username}")
    fun findUserByUsername(@PathVariable username: String): ResponseEntity<User> {
        val user = userService.getUserByUsername(username)
        return ResponseEntity.ok(user)
    }

    @PostMapping
    fun createUser(@Valid @RequestBody user: UserDtoRequest): ResponseEntity<UserDtoResponse> {
        val createdUser = userService.createUser(user)
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser)
    }

    @DeleteMapping("/{username}")
    fun deleteUser(@PathVariable username: String): ResponseEntity<Unit> {
        userService.deleteUserByUsername(username)
        return ResponseEntity.ok().build()
    }

    @PostMapping("/{username}/to/{publisherName}")
    fun subscribe(
        @PathVariable("username") username: String,
        @PathVariable("publisherName") publisherName: String
    ): ResponseEntity<SubscriptionDtoResponse> {
        val newSubscription = userService.subscribe(username, publisherName)
        return ResponseEntity.status(HttpStatus.CREATED).body(newSubscription)
    }

    @PostMapping("/{username}/becomePublisher")
    fun becomePublisher(
        @PathVariable("username") username: String,
        @Valid @RequestBody publisherDtoRequest: PublisherDtoRequest
    ): ResponseEntity<PublisherDtoResponse> {
        return ResponseEntity.status(
            HttpStatus.CREATED
        ).body(userService.becamePublisher(username, publisherDtoRequest))
    }

    @GetMapping("/{username}/posts")
    fun getContentFromCreators(
        @PathVariable("username") username: String,
        @RequestParam(name = "page", defaultValue = "1") page: Int
    ): ResponseEntity<List<PostDtoResponse>> {
        val posts = userService.getPostsFromFollowedCreators(username, page)
        return ResponseEntity.status(
            HttpStatus.OK
        ).body(posts)
    }
}
