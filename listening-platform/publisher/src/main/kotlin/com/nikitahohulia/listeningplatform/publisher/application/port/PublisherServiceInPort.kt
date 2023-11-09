package com.nikitahohulia.listeningplatform.publisher.application.port

import com.nikitahohulia.listeningplatform.post.domain.Post
import com.nikitahohulia.listeningplatform.publisher.domain.Publisher
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface PublisherServiceInPort {

    fun getPublisherByPublisherName(publisherName: String): Mono<Publisher>

    fun createPublisher(publisher: Publisher): Mono<Publisher>

    fun getAllPublishers(): Flux<Publisher>

    fun deletePublisher(publisherName: String): Mono<Unit>

    fun postContent(publisherName: String, content: String): Mono<Post>

    fun getPostsByPublisherName(publisherName: String): Flux<Post>
}
