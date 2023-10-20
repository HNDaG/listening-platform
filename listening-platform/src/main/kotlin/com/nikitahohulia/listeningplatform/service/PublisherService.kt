package com.nikitahohulia.listeningplatform.service

import com.nikitahohulia.listeningplatform.entity.Post
import com.nikitahohulia.listeningplatform.entity.Publisher
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface PublisherService {

    fun getPublisherByUsername(username: String): Mono<Publisher>

    fun getPublisherByPublisherName(publisherName: String): Mono<Publisher>

    fun createPublisher(publisher: Publisher): Mono<Publisher>

    fun getAllPublishers(): Flux<Publisher>

    fun deletePublisher(publisherName: String): Mono<Unit>

    fun postContent(publisherName: String, content: String): Mono<Post>

    fun getPostsByPublisherName(publisherName: String): Flux<Post>
}
