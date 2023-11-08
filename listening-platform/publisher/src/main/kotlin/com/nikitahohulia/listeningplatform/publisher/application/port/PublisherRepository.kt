package com.nikitahohulia.listeningplatform.publisher.application.port

import org.bson.types.ObjectId
import com.nikitahohulia.listeningplatform.publisher.domain.Publisher
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface PublisherRepository {

    fun findById(id: ObjectId): Mono<Publisher>

    fun findAll(): Flux<Publisher>

    fun save(publisher: Publisher): Mono<Publisher>

    fun findByPublisherName(publisherName: String): Mono<Publisher>

    fun deleteByPublisherName(publisherName: String): Mono<Long>
}
