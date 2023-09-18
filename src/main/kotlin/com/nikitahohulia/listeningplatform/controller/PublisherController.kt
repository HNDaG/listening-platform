package com.nikitahohulia.listeningplatform.controller

import com.nikitahohulia.listeningplatform.bpp.LogOnException
import com.nikitahohulia.listeningplatform.dto.request.PublisherDtoRequest
import com.nikitahohulia.listeningplatform.dto.response.PostDtoResponse
import com.nikitahohulia.listeningplatform.dto.response.PublisherDtoResponse
import com.nikitahohulia.listeningplatform.service.PublisherService
import jakarta.validation.Valid
import jakarta.validation.constraints.NotEmpty
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@LogOnException
@RestController
@RequestMapping("/api/V1/publishers")
class PublisherController(private val publisherService: PublisherService) {

    @GetMapping
    fun findAllPublishers(): ResponseEntity<List<PublisherDtoResponse>> {
        val users = publisherService.getAllPublishers()
        return ResponseEntity.ok(users)
    }

    @GetMapping("/by/publisherName/{publisherName}")
    fun findPublisherByName(@PathVariable publisherName: String): ResponseEntity<PublisherDtoResponse> {
        val publisher = publisherService.getPublisherByPublisherName(publisherName)
        return ResponseEntity.ok(publisher)
    }

    @GetMapping("/by/username/{username}")
    fun findPublisherByUsername(@PathVariable username: String): ResponseEntity<PublisherDtoResponse> {
        val publisher = publisherService.getPublisherByUsername(username)
        return ResponseEntity.ok(publisher)
    }

    @PostMapping
    fun createPublisher(@Valid @RequestBody publisher: PublisherDtoRequest): ResponseEntity<PublisherDtoResponse> {
        val createdPublisher = publisherService.createPublisher(publisher)
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPublisher)
    }

    @DeleteMapping("/{publisherName}")
    fun deletePublisher(@PathVariable publisherName: String): ResponseEntity<Unit> {
        publisherService.deletePublisher(publisherName)
        return ResponseEntity.ok().build()
    }

    @PostMapping("/{publisherName}/post")
    fun makePost(
        @PathVariable publisherName: String,
        @RequestBody @NotEmpty content: String
    ): ResponseEntity<PostDtoResponse> {
        val createdPost = publisherService.postContent(publisherName, content)
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPost)
    }

    @GetMapping("/posts/{publisherName}")
    fun getPublishersPosts(@PathVariable publisherName: String): ResponseEntity<List<PostDtoResponse>> {
        val posts = publisherService.getPostsByPublisherName(publisherName)
        return ResponseEntity.ok(posts)
    }
}
