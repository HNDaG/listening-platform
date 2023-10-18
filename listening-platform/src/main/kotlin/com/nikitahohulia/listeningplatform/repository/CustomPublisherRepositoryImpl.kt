package com.nikitahohulia.listeningplatform.repository

import com.nikitahohulia.listeningplatform.entity.Publisher
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.aggregation.Aggregation
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
class CustomPublisherRepositoryImpl(private val mongoTemplate: ReactiveMongoTemplate) : CustomPublisherRepository {

    val collectionName = "publisher"

    override fun findById(id: ObjectId): Mono<Publisher> {
        val query = Query().addCriteria(Criteria.where("id").`is`(id))
        return mongoTemplate.findOne(query, Publisher::class.java, collectionName)
    }

    override fun findAll(): Flux<Publisher> {
        return mongoTemplate.findAll(Publisher::class.java, collectionName)
    }

    override fun save(publisher: Publisher): Mono<Publisher> {
        return mongoTemplate.save(publisher, collectionName)
    }

    override fun findByPublisherName(publisherName: String): Mono<Publisher> {
        val query = Query().addCriteria(Criteria.where("publisherName").`is`(publisherName))
        return mongoTemplate.findOne(query, Publisher::class.java, collectionName)
    }

    override fun deleteByPublisherName(publisherName: String): Mono<Long> {
        val query = Query().addCriteria(Criteria.where("publisherName").`is`(publisherName))
        return mongoTemplate.remove(query, Publisher::class.java, collectionName).map { it.deletedCount }
    }
}
