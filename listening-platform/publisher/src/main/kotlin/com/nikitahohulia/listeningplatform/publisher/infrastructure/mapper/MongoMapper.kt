package com.nikitahohulia.listeningplatform.publisher.infrastructure.mapper

import com.nikitahohulia.listeningplatform.publisher.domain.Publisher
import com.nikitahohulia.listeningplatform.publisher.infrastructure.repository.entity.MongoPublisher

fun Publisher.toMongo() = MongoPublisher(
    id = this.id,
    publisherName = this.publisherName,
    password = this.password,
    rating = this.rating
)

fun MongoPublisher.toEntity() = Publisher(
    id = this.id,
    publisherName = this.publisherName,
    password = this.password,
    rating = this.rating
)
