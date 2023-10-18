package com.nikitahohulia.listeningplatform.repository

import com.nikitahohulia.listeningplatform.entity.Publisher
import org.bson.types.ObjectId
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface CustomPublisherRepository {

    fun findById(id: ObjectId): Mono<Publisher>

    fun findAll(): Flux<Publisher>

    fun save(publisher: Publisher): Mono<Publisher>

    fun findByPublisherName(publisherName: String): Mono<Publisher>

    fun deleteByPublisherName(publisherName: String): Mono<Long>
}
