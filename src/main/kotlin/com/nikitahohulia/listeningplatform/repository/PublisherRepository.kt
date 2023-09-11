package com.nikitahohulia.listeningplatform.repository

import com.nikitahohulia.listeningplatform.entity.Publisher
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface PublisherRepository : JpaRepository<Publisher, Long> {
    fun findByPublisherName(publisherName: String): Publisher?
}
