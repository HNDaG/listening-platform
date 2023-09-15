package com.nikitahohulia.listeningplatform.repository

import com.nikitahohulia.listeningplatform.entity.Subscription
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface SubscriptionRepository : MongoRepository<Subscription, ObjectId>
