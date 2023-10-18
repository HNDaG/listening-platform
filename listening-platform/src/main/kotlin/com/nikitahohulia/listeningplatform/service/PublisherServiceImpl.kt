package com.nikitahohulia.listeningplatform.service

import reactor.kotlin.core.publisher.switchIfEmpty
import com.nikitahohulia.listeningplatform.dto.request.PostDtoRequest
import com.nikitahohulia.listeningplatform.dto.request.PublisherDtoRequest
import com.nikitahohulia.listeningplatform.dto.request.toEntity
import com.nikitahohulia.listeningplatform.dto.response.PostDtoResponse
import com.nikitahohulia.listeningplatform.dto.response.PublisherDtoResponse
import com.nikitahohulia.listeningplatform.dto.response.toResponse
import com.nikitahohulia.listeningplatform.entity.Post
import com.nikitahohulia.listeningplatform.entity.Publisher
import com.nikitahohulia.listeningplatform.exception.DuplicateException
import com.nikitahohulia.listeningplatform.exception.EntityNotFoundException
import com.nikitahohulia.listeningplatform.exception.NotFoundException
import com.nikitahohulia.listeningplatform.repository.CustomPostRepository
import com.nikitahohulia.listeningplatform.repository.CustomUserRepository
import com.nikitahohulia.listeningplatform.repository.CustomPublisherRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Service
class PublisherServiceImpl(
    private val publisherRepository: CustomPublisherRepository,
    private val postRepository: CustomPostRepository,
    private val userRepository: CustomUserRepository
) : PublisherService {

    override fun getPublisherByUsername(username: String): Mono<PublisherDtoResponse> {
        val userMono = userRepository.findByUsername(username)
            .switchIfEmpty {
                NotFoundException("User not found with given username = $username")
                    .toMono()
            }
        return userMono
            .flatMap { user ->
                val publisherId = user.publisherId
                if (publisherId != null) {
                    publisherRepository.findById(publisherId)
                        .map { it.toResponse() }
                        .switchIfEmpty {
                            NotFoundException("Publisher not found with given id = ${publisherId.toHexString()}")
                                .toMono()
                        }
                } else {
                    NotFoundException("User is not a publisher").toMono()
                }
            }
    }

    override fun getPublisherByPublisherName(publisherName: String): Mono<PublisherDtoResponse> {
        return publisherRepository.findByPublisherName(publisherName).map { it.toResponse() }
            .switchIfEmpty {
                NotFoundException("Publisher not found with given publisherName = $publisherName")
                    .toMono()
            }
    }

    override fun createPublisher(publisherDtoRequest: PublisherDtoRequest): Mono<PublisherDtoResponse> {
        return publisherRepository.findByPublisherName(publisherDtoRequest.publisherName)
            .handle<PublisherDtoResponse> { _, sync ->
                sync.error(DuplicateException("Publisher already exists"))
            }
            .switchIfEmpty(
                publisherRepository.save(publisherDtoRequest.toEntity()).map { it.toResponse() }
            )
    }


    override fun getAllPublishers(): Flux<PublisherDtoResponse> {
        return publisherRepository.findAll().map { it.toResponse() }
    }

    override fun deletePublisher(publisherName: String): Mono<Unit> {
        return publisherRepository.findByPublisherName(publisherName)
            .flatMap { publisher ->
                publisher.id?.let { userRepository.findByPublisherId(it) }
                    ?: Mono.empty()
            }
            .flatMap { user ->
                val updatedUser = user.copy(publisherId = null)
                userRepository.save(updatedUser)
            }
            .then(
                publisherRepository.deleteByPublisherName(publisherName)
                    .handle { deletedCount, sync ->
                        if (deletedCount == 0L) {
                            sync.error(
                                EntityNotFoundException("Publisher with publisherName - $publisherName not found")
                            )
                        }
                        else Mono.just(Unit)
                    }
            )
    }

    override fun postContent(publisherName: String, content: String): Mono<PostDtoResponse> {
        return publisherRepository.findByPublisherName(publisherName)
            .switchIfEmpty {
                NotFoundException("Publisher not found with given publisherName = $publisherName")
                    .toMono()
            }
            .flatMap { publisher ->
                val postDtoRequest = PostDtoRequest(creatorId = publisher.id!!.toHexString(), content = content)
                postRepository.save(postDtoRequest.toEntity())
            }
            .map { it.toResponse() }
            .switchIfEmpty {
                NotFoundException("Internal error, failed to save new post")
                    .toMono()
            }
    }

    override fun getPostsByPublisherName(publisherName: String): Flux<PostDtoResponse> {
        return publisherRepository.findByPublisherName(publisherName)
            .switchIfEmpty {
                NotFoundException("Publisher not found with given publisherName = $publisherName")
                    .toMono()
            }
            .flatMapMany { publisher -> findAllPostsByCreatorIdOrderByCreatedAt(publisher) }
            .map { post -> post.toResponse() }
    }

    private fun findAllPostsByCreatorIdOrderByCreatedAt(publisher: Publisher): Flux<Post> {
        val publisherId = publisher.id!!
        return postRepository.findAllPostsByCreatorIdOrderByCreatedAt(publisherId)
            .switchIfEmpty(
                NotFoundException("Publisher not found with given id = ${publisherId.toHexString()}")
                    .toMono()
            )
    }
}
