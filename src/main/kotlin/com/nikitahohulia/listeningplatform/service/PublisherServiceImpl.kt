package com.nikitahohulia.listeningplatform.service

import com.nikitahohulia.listeningplatform.dto.request.PostDtoRequest
import com.nikitahohulia.listeningplatform.dto.request.PublisherDtoRequest
import com.nikitahohulia.listeningplatform.dto.request.toEntity
import com.nikitahohulia.listeningplatform.dto.response.PostDtoResponse
import com.nikitahohulia.listeningplatform.dto.response.PublisherDtoResponse
import com.nikitahohulia.listeningplatform.dto.response.toResponse
import com.nikitahohulia.listeningplatform.exception.DuplicateException
import com.nikitahohulia.listeningplatform.exception.NotFoundException
import com.nikitahohulia.listeningplatform.repository.CustomPostRepository
import com.nikitahohulia.listeningplatform.repository.CustomUserRepository
import com.nikitahohulia.listeningplatform.repository.PublisherRepository
import org.springframework.stereotype.Service

@Service
class PublisherServiceImpl(
    private val publisherRepository: PublisherRepository,
    private val postRepository: CustomPostRepository,
    private val userRepository: CustomUserRepository
) : PublisherService {

    override fun getPublisherByUsername(username: String): PublisherDtoResponse {
        val publisherId = userRepository.findByUsername(username)?.publisherId
            ?: throw NotFoundException("User not found with given username = $username")
        return publisherRepository.findById(publisherId)
            .orElseThrow { throw NotFoundException("Publisher not found with given id = ${publisherId.toHexString()}") }
            .toResponse()
    }

    override fun getPublisherByPublisherName(publisherName: String): PublisherDtoResponse {
        val publisher = publisherRepository.findByPublisherName(publisherName)
            ?: throw NotFoundException("Publisher not found with given publisherName = $publisherName")
        return publisher.toResponse()
    }

    override fun createPublisher(publisherDtoRequest: PublisherDtoRequest): PublisherDtoResponse {
        val existingPublisher = publisherRepository.findByPublisherName(publisherDtoRequest.publisherName)
        if (existingPublisher != null) {
            throw DuplicateException("Publisher with the same PublisherName already exists")
        }
        val newPublisher = publisherRepository.save(publisherDtoRequest.toEntity())
        return newPublisher.toResponse()
    }

    override fun getAllPublishers(): List<PublisherDtoResponse> {
        return publisherRepository.findAll().map { it.toResponse() }
    }

    override fun deletePublisher(publisherName: String) {
        publisherRepository.deleteByPublisherName(publisherName)
    }

    override fun postContent(publisherName: String, content: String): PostDtoResponse {
        val publisher = publisherRepository.findByPublisherName(publisherName)
            ?: throw NotFoundException("Publisher not found with given publisherName = $publisherName")
        val creatorId = publisher.id?.toHexString() ?: throw NotFoundException("Publisher ID is null")
        val postDtoRequest = PostDtoRequest(creatorId = creatorId, content = content)
        val newPost = postRepository.save(postDtoRequest.toEntity())
        return newPost?.toResponse() ?: throw NotFoundException("Internal error, failed to save new post")
    }

    override fun getPostsByPublisherName(publisherName: String): List<PostDtoResponse> {
        val publisher = publisherRepository.findByPublisherName(publisherName)
            ?: throw NotFoundException("Publisher not found with given publisherName = $publisherName")
        return publisher.id?.let { postRepository.findAllPostsByCreatorIdOrderByCreatedAt(it) }
            .orEmpty()
            .map { it.toResponse() }
    }
}
