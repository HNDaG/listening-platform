package com.nikitahohulia.listeningplatform.service

import com.nikitahohulia.listeningplatform.dto.request.PublisherDtoRequest
import com.nikitahohulia.listeningplatform.dto.response.PostDtoResponse
import com.nikitahohulia.listeningplatform.dto.response.PublisherDtoResponse

interface PublisherService {

    fun getPublisherByUsername(username: String): PublisherDtoResponse

    fun getPublisherByPublisherName(publisherName: String): PublisherDtoResponse

    fun createPublisher(publisherDtoRequest: PublisherDtoRequest): PublisherDtoResponse

    fun getAllPublishers(): List<PublisherDtoResponse>

    fun deletePublisher(publisherName: String)

    fun postContent(publisherName: String, content: String): PostDtoResponse

    fun getPostsByPublisherName(publisherName: String): List<PostDtoResponse>
}
