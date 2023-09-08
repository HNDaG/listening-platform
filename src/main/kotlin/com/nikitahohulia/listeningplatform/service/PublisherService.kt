package com.nikitahohulia.listeningplatform.service

import com.nikitahohulia.listeningplatform.entity.Post
import com.nikitahohulia.listeningplatform.entity.Publisher

interface PublisherService {
    fun getPublisherById(id: Long): Publisher?
    fun getPublisherByPublisherName(publisherName: String): Publisher?
    fun createPublisher(publisher: Publisher): Publisher
    fun getAllPublishers(): List<Publisher>
    fun deletePublisher(id: Long)
    fun postContent(post: Post): Post
}
