package com.nikitahohulia.listeningplatform.service

import com.nikitahohulia.listeningplatform.dto.request.PublisherDtoRequest
import com.nikitahohulia.listeningplatform.dto.response.PostDtoResponse
import com.nikitahohulia.listeningplatform.dto.response.PublisherDtoResponse
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface PublisherService {

    fun getPublisherByUsername(username: String): Mono<PublisherDtoResponse>

    fun getPublisherByPublisherName(publisherName: String): Mono<PublisherDtoResponse>

    fun createPublisher(publisherDtoRequest: PublisherDtoRequest): Mono<PublisherDtoResponse>

    fun getAllPublishers(): Flux<PublisherDtoResponse>

    fun deletePublisher(publisherName: String): Mono<Unit>

    fun postContent(publisherName: String, content: String): Mono<PostDtoResponse>

    fun getPostsByPublisherName(publisherName: String): Flux<PostDtoResponse>
}
