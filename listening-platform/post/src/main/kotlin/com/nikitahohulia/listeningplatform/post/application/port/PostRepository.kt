package com.nikitahohulia.listeningplatform.post.application.port

import com.nikitahohulia.listeningplatform.post.domain.Post
import org.bson.types.ObjectId
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface PostRepository {

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
