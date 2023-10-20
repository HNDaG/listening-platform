package com.nikitahohulia.listeningplatform.repository

import com.nikitahohulia.listeningplatform.entity.Post
import org.bson.types.ObjectId
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.find
import org.springframework.data.mongodb.core.findAll
import org.springframework.data.mongodb.core.findOne
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.remove
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
class CustomPostRepositoryImpl(private val mongoTemplate: ReactiveMongoTemplate) : CustomPostRepository {

    override fun findPostById(id: ObjectId): Mono<Post> {
        val query = Query().addCriteria(Criteria.where("id").`is`(id))
        return mongoTemplate.findOne<Post>(query)
    }

    override fun findAll(): Flux<Post> {
        return mongoTemplate.findAll<Post>()
    }

    override fun findAllByCreatorId(id: ObjectId): Flux<Post> {
        val query = Query().addCriteria(Criteria.where("creatorId").`is`(id))
        return mongoTemplate.find<Post>(query)
    }

    override fun save(post: Post): Mono<Post> {
        return mongoTemplate.save(post)
    }

    override fun deleteById(id: ObjectId): Mono<Long> {
        val query = Query().addCriteria(Criteria.where("id").`is`(id))
        return mongoTemplate.remove<Post>(query).map { it.deletedCount }
    }

    override fun deleteByPublisherId(id: ObjectId): Mono<Long> {
        val query = Query().addCriteria(Criteria.where("creatorId").`is`(id))
        return mongoTemplate.remove<Post>(query).map { it.deletedCount }
    }

    override fun findAllPostsByCreatorIdOrderByCreatedAt(creatorId: ObjectId): Flux<Post> {
        val query = Query().addCriteria(Criteria.where("creatorId").`is`(creatorId))
        query.with(Sort.by(Sort.Order.desc("createdAt")))
        return mongoTemplate.find<Post>(query)
    }

    override fun findAllPublisherIdByUserId(userId: ObjectId): Flux<ObjectId> {
        val query = Query().addCriteria(Criteria.where("creatorId").`is`(userId))
        query.fields().include("creatorId")
        return mongoTemplate.find<Post>(query).mapNotNull { it.creatorId }
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

            mongoTemplate.find<Post>(query)
        }
    }
}
