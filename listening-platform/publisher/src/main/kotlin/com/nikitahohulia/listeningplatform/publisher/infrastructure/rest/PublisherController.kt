package com.nikitahohulia.listeningplatform.publisher.infrastructure.rest

import com.nikitahohulia.listeningplatform.core.infrastructure.annotation.LogOnException
import com.nikitahohulia.listeningplatform.post.infrastructure.dto.response.PostDtoResponse
import com.nikitahohulia.listeningplatform.post.infrastructure.mapper.toResponse
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
import com.nikitahohulia.listeningplatform.publisher.application.port.PublisherServiceInPort
import com.nikitahohulia.listeningplatform.publisher.infrastructure.rest.dto.request.PublisherDtoRequest
import com.nikitahohulia.listeningplatform.publisher.infrastructure.rest.dto.response.PublisherDtoResponse
import com.nikitahohulia.listeningplatform.publisher.infrastructure.mapper.toEntity
import com.nikitahohulia.listeningplatform.publisher.infrastructure.mapper.toResponse
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@LogOnException
@RestController
@RequestMapping("/api/V2/publishers")
class PublisherController(private val publisherOperationsInPort: PublisherServiceInPort) {

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    fun findAllPublishers(): Flux<PublisherDtoResponse> {
        return publisherOperationsInPort.getAllPublishers().map { it.toResponse() }
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/by/publisherName/{publisherName}")
    fun findPublisherByName(@PathVariable publisherName: String): Mono<PublisherDtoResponse> {
        return publisherOperationsInPort.getPublisherByPublisherName(publisherName).map { it.toResponse() }
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    fun createPublisher(@Valid @RequestBody publisherDtoRequest: PublisherDtoRequest): Mono<PublisherDtoResponse> {
        return publisherOperationsInPort.createPublisher(publisherDtoRequest.toEntity()).map { it.toResponse() }
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{publisherName}")
    fun deletePublisher(@PathVariable publisherName: String): Mono<Unit> {
        return publisherOperationsInPort.deletePublisher(publisherName)
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{publisherName}/post")
    fun makePost(
        @PathVariable publisherName: String,
        @RequestBody @NotEmpty content: String
    ): Mono<PostDtoResponse> {
        return publisherOperationsInPort.postContent(publisherName, content).map { it.toResponse() }
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/posts/{publisherName}")
    fun getPublishersPosts(@PathVariable publisherName: String): Flux<PostDtoResponse> {
        return publisherOperationsInPort.getPostsByPublisherName(publisherName).map { it.toResponse() }
    }
}
