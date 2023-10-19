package com.nikitahohulia.listeningplatform.repository

import com.nikitahohulia.listeningplatform.entity.Post
import org.bson.types.ObjectId
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
class CustomPostRepositoryImpl(private val mongoTemplate: ReactiveMongoTemplate) : CustomPostRepository {

    override fun findPostById(id: ObjectId): Mono<Post> {
        val query = Query().addCriteria(Criteria.where("id").`is`(id))
        return mongoTemplate.findOne(query, Post::class.java, Post.COLLECTION_NAME)
    }

    override fun findAll(): Flux<Post> {
        return mongoTemplate.findAll(Post::class.java, Post.COLLECTION_NAME)
    }

    override fun findAllByCreatorId(id: ObjectId): Flux<Post> {
        val query = Query().addCriteria(Criteria.where("creatorId").`is`(id))
        return mongoTemplate.find(query, Post::class.java, Post.COLLECTION_NAME)
    }

    override fun save(post: Post): Mono<Post> {
        return mongoTemplate.save(post, Post.COLLECTION_NAME)
    }

    override fun deleteById(id: ObjectId): Mono<Long> {
        val query = Query().addCriteria(Criteria.where("id").`is`(id))
        return mongoTemplate.remove(query, Post::class.java).map { it.deletedCount }
    }

    override fun deleteByPublisherId(id: ObjectId): Mono<Long> {
        val query = Query().addCriteria(Criteria.where("creatorId").`is`(id))
        return mongoTemplate.remove(query, Post::class.java).map { it.deletedCount }
    }

    override fun findAllPostsByCreatorIdOrderByCreatedAt(creatorId: ObjectId): Flux<Post> {
        val query = Query().addCriteria(Criteria.where("creatorId").`is`(creatorId))
        query.with(Sort.by(Sort.Order.desc("createdAt")))
        return mongoTemplate.find(query, Post::class.java, Post.COLLECTION_NAME)
    }

    override fun findAllPublisherIdByUserId(userId: ObjectId): Flux<ObjectId> {
        val query = Query().addCriteria(Criteria.where("creatorId").`is`(userId))
        query.fields().include("creatorId")
        return mongoTemplate.find(query, Post::class.java, Post.COLLECTION_NAME).mapNotNull { it.creatorId }
    }

    override fun findAllBySubscriptionIds(subscriptionIds: Flux<ObjectId>, page: Int, size: Int): Flux<Post> {
        val skip = ((page - 1) * size).toLong()

        return subscriptionIds.collectList().
        flatMapMany { ids ->
            val criteria = Criteria.where("creatorId").`in`(ids)
            val query = Query(criteria)
                .with(Sort.by(Sort.Direction.DESC, "createdAt"))
                .skip(skip)
                .limit(size)

            mongoTemplate.find(query, Post::class.java, Post.COLLECTION_NAME)
        }
    }
}
