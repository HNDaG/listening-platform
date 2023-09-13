package com.nikitahohulia.listeningplatform.repository

import com.nikitahohulia.listeningplatform.entity.Subscription
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SubscriptionRepository : JpaRepository<Subscription, Long>
