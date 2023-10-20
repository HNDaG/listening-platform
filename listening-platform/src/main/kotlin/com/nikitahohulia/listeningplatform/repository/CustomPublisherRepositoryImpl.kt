package com.nikitahohulia.listeningplatform.repository

import com.nikitahohulia.listeningplatform.entity.Publisher
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.findAll
import org.springframework.data.mongodb.core.findOne
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.remove
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
class CustomPublisherRepositoryImpl(private val mongoTemplate: ReactiveMongoTemplate) : CustomPublisherRepository {

    override fun findById(id: ObjectId): Mono<Publisher> {
        val query = Query().addCriteria(Criteria.where("id").`is`(id))
        return mongoTemplate.findOne<Publisher>(query)
    }

    override fun findAll(): Flux<Publisher> {
        return mongoTemplate.findAll<Publisher>()
    }

    override fun save(publisher: Publisher): Mono<Publisher> {
        return mongoTemplate.save(publisher)
    }

    override fun findByPublisherName(publisherName: String): Mono<Publisher> {
        val query = Query().addCriteria(Criteria.where("publisherName").`is`(publisherName))
        return mongoTemplate.findOne<Publisher>(query)
    }

    override fun deleteByPublisherName(publisherName: String): Mono<Long> {
        val query = Query().addCriteria(Criteria.where("publisherName").`is`(publisherName))
        return mongoTemplate.remove<Publisher>(query).map { it.deletedCount }
    }
}
