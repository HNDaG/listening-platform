package com.nikitahohulia.listeningplatform.repository

import com.nikitahohulia.listeningplatform.entity.Publisher
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
class CustomPublisherRepositoryImpl(private val mongoTemplate: ReactiveMongoTemplate) : CustomPublisherRepository {

    override fun findById(id: ObjectId): Mono<Publisher> {
        val query = Query().addCriteria(Criteria.where("id").`is`(id))
        return mongoTemplate.findOne(query, Publisher::class.java, Publisher.COLLECTION_NAME)
    }

    override fun findAll(): Flux<Publisher> {
        return mongoTemplate.findAll(Publisher::class.java, Publisher.COLLECTION_NAME)
    }

    override fun save(publisher: Publisher): Mono<Publisher> {
        return mongoTemplate.save(publisher, Publisher.COLLECTION_NAME)
    }

    override fun findByPublisherName(publisherName: String): Mono<Publisher> {
        val query = Query().addCriteria(Criteria.where("publisherName").`is`(publisherName))
        return mongoTemplate.findOne(query, Publisher::class.java, Publisher.COLLECTION_NAME)
    }

    override fun deleteByPublisherName(publisherName: String): Mono<Long> {
        val query = Query().addCriteria(Criteria.where("publisherName").`is`(publisherName))
        return mongoTemplate.remove(query, Publisher::class.java, Publisher.COLLECTION_NAME).map { it.deletedCount }
    }
}
