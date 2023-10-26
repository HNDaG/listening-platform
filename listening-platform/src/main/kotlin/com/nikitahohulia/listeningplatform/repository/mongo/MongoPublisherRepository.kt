package com.nikitahohulia.listeningplatform.repository.mongo

import com.nikitahohulia.listeningplatform.entity.Publisher
import com.nikitahohulia.listeningplatform.entity.mongo.MongoPublisher
import com.nikitahohulia.listeningplatform.entity.mongo.toEntity
import com.nikitahohulia.listeningplatform.entity.mongo.toMongo
import com.nikitahohulia.listeningplatform.repository.PublisherRepository
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
class MongoPublisherRepository(private val mongoTemplate: ReactiveMongoTemplate) : PublisherRepository {

    override fun findById(id: ObjectId): Mono<Publisher> {
        val query = Query().addCriteria(Criteria.where("id").`is`(id))
        return mongoTemplate.findOne<MongoPublisher>(query)
            .map { it.toEntity() }
    }

    override fun findAll(): Flux<Publisher> {
        return mongoTemplate.findAll<MongoPublisher>()
            .map { it.toEntity() }
    }

    override fun save(publisher: Publisher): Mono<Publisher> {
        return mongoTemplate.save(publisher.toMongo())
            .map { it.toEntity() }
    }

    override fun findByPublisherName(publisherName: String): Mono<Publisher> {
        val query = Query().addCriteria(Criteria.where("publisherName").`is`(publisherName))
        return mongoTemplate.findOne<MongoPublisher>(query)
            .map { it.toEntity() }
    }

    override fun deleteByPublisherName(publisherName: String): Mono<Long> {
        val query = Query().addCriteria(Criteria.where("publisherName").`is`(publisherName))
        return mongoTemplate.remove<MongoPublisher>(query).map { it.deletedCount }
    }
}
