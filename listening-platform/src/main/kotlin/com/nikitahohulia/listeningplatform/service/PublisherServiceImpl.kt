package com.nikitahohulia.listeningplatform.service

import reactor.kotlin.core.publisher.switchIfEmpty
import com.nikitahohulia.listeningplatform.entity.Post
import com.nikitahohulia.listeningplatform.entity.Publisher
import com.nikitahohulia.listeningplatform.exception.DuplicateException
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

    override fun getPublisherByUsername(username: String): Mono<Publisher> {
        return userRepository.findByUsername(username)
            .mapNotNull { it.publisherId }
            .flatMap { publisherRepository.findById(it!!) }
            .switchIfEmpty { NotFoundException("Publisher with username=$username not found").toMono() }
    }

    override fun getPublisherByPublisherName(publisherName: String): Mono<Publisher> {
        return publisherRepository.findByPublisherName(publisherName)
            .switchIfEmpty { 
                NotFoundException("Publisher not found with given publisherName = $publisherName").toMono() 
            }
    }

    override fun createPublisher(publisher: Publisher): Mono<Publisher> {
        return publisherRepository.findByPublisherName(publisher.publisherName)
            .handle<Publisher> { _, sync -> sync.error(DuplicateException("Publisher already exists")) }
            .switchIfEmpty(publisherRepository.save(publisher))
    }


    override fun getAllPublishers(): Flux<Publisher> {
        return publisherRepository.findAll()
    }

    override fun deletePublisher(publisherName: String): Mono<Unit> {
        return publisherRepository.findByPublisherName(publisherName)
            .flatMap { publisher -> publisher.id?.let { userRepository.findByPublisherId(it) } ?: Mono.empty() }
            .flatMap { user -> userRepository.save(user.copy(publisherId = null)) }
            .then(publisherRepository.deleteByPublisherName(publisherName)
                    .handle { deletedCount, sync ->
                        if (deletedCount == 0L) {
                            sync.error(NotFoundException("Publisher with publisherName - $publisherName not found"))
                        } else Mono.just(Unit)
                    }
            )
    }

    override fun postContent(publisherName: String, content: String): Mono<Post> {
        return publisherRepository.findByPublisherName(publisherName)
            .flatMap { publisher -> postRepository.save(Post(creatorId = publisher.id!!, content = content)) }
            .switchIfEmpty { NotFoundException("Internal error, failed to save new post").toMono() }
    }

    override fun getPostsByPublisherName(publisherName: String): Flux<Post> {
        return publisherRepository.findByPublisherName(publisherName)
            .switchIfEmpty { NotFoundException("Publisher not found with given publisherName = $publisherName")
                .toMono() }
            .flatMapMany { publisher -> findAllPostsByCreatorIdOrderByCreatedAt(publisher) }
    }

    private fun findAllPostsByCreatorIdOrderByCreatedAt(publisher: Publisher): Flux<Post> {
        val publisherId = publisher.id!!
        return postRepository.findAllPostsByCreatorIdOrderByCreatedAt(publisherId)
            .switchIfEmpty (NotFoundException("Publisher not found with given id = ${publisherId.toHexString()}")
                .toMono())
    }
}
