package com.nikitahohulia.listeningplatform.repository

import com.nikitahohulia.listeningplatform.entity.Publisher
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface PublisherRepository : MongoRepository<Publisher, ObjectId> {

    fun findByPublisherName(publisherName: String): Publisher?

    fun deleteByPublisherName(publisherName: String)
}
