package com.nikitahohulia.listeningplatform.repository

import com.nikitahohulia.listeningplatform.entity.Post
import org.bson.types.ObjectId
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface CustomPostRepository {

    fun findPostById(id: ObjectId): Mono<Post>

    fun findAll(): Flux<Post>

    fun findAllByCreatorId(id: ObjectId): Flux<Post>

    fun save(post: Post): Mono<Post>

    fun deleteById(id: ObjectId): Mono<Long>

    fun deleteByPublisherId(id: ObjectId): Mono<Long>

    fun findAllPostsByCreatorIdOrderByCreatedAt(creatorId: ObjectId): Flux<Post>

    fun findAllPublisherIdByUserId(userId: ObjectId): Flux<ObjectId>

    fun findAllBySubscriptionIds(subscriptionIds: Flux<ObjectId>, page: Int, size: Int): Flux<Post>
}
