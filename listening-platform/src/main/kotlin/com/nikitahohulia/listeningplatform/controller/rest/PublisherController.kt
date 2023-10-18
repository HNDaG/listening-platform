package com.nikitahohulia.listeningplatform.controller.rest

import com.nikitahohulia.listeningplatform.bpp.annotation.LogOnException
import com.nikitahohulia.listeningplatform.dto.request.PublisherDtoRequest
import com.nikitahohulia.listeningplatform.dto.response.PostDtoResponse
import com.nikitahohulia.listeningplatform.dto.response.PublisherDtoResponse
import com.nikitahohulia.listeningplatform.service.PublisherService
import jakarta.validation.Valid
import jakarta.validation.constraints.NotEmpty
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
@RequestMapping("/api/V1/publishers")
class PublisherController(private val publisherService: PublisherService) {

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    fun findAllPublishers(): Flux<PublisherDtoResponse> {
        return publisherService.getAllPublishers()
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/by/publisherName/{publisherName}")
    fun findPublisherByName(@PathVariable publisherName: String): Mono<PublisherDtoResponse> {
        return publisherService.getPublisherByPublisherName(publisherName)
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/by/username/{username}")
    fun findPublisherByUsername(@PathVariable username: String): Mono<PublisherDtoResponse> {
        return publisherService.getPublisherByUsername(username)
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    fun createPublisher(@Valid @RequestBody publisher: PublisherDtoRequest): Mono<PublisherDtoResponse> {
        return publisherService.createPublisher(publisher)
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{publisherName}")
    fun deletePublisher(@PathVariable publisherName: String): Mono<Unit> {
        return publisherService.deletePublisher(publisherName)
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{publisherName}/post")
    fun makePost(
        @PathVariable publisherName: String,
        @RequestBody @NotEmpty content: String
    ): Mono<PostDtoResponse> {
        return publisherService.postContent(publisherName, content)
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/posts/{publisherName}")
    fun getPublishersPosts(@PathVariable publisherName: String): Flux<PostDtoResponse> {
        return publisherService.getPostsByPublisherName(publisherName)
    }
}
