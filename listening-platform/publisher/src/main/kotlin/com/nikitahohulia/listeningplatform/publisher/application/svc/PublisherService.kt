package com.nikitahohulia.listeningplatform.publisher.application.svc

import com.nikitahohulia.listeningplatform.core.application.exception.DuplicateException
import com.nikitahohulia.listeningplatform.core.application.exception.NotFoundException
import reactor.kotlin.core.publisher.switchIfEmpty
import com.nikitahohulia.listeningplatform.post.application.port.PostRepositoryOutPort
import com.nikitahohulia.listeningplatform.post.domain.Post
import org.springframework.stereotype.Service
import com.nikitahohulia.listeningplatform.publisher.application.port.PublisherRepositoryOutPort
import com.nikitahohulia.listeningplatform.publisher.application.port.PublisherServiceInPort
import com.nikitahohulia.listeningplatform.publisher.domain.Publisher
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Service
class PublisherService(
    private val publisherRepositoryOutPort: PublisherRepositoryOutPort,
    private val postRepositoryOutPort: PostRepositoryOutPort,
) : PublisherServiceInPort {

    override fun getPublisherByPublisherName(publisherName: String): Mono<Publisher> {
        return publisherRepositoryOutPort.findByPublisherName(publisherName)
            .switchIfEmpty { NotFoundException("Publisher not found with given publisherName = $publisherName")
                .toMono()
            }
    }

    override fun createPublisher(publisher: Publisher): Mono<Publisher> {
        return publisherRepositoryOutPort.findByPublisherName(publisher.publisherName)
            .handle<Publisher> { _, sync -> sync.error(DuplicateException("Publisher already exists")) }
            .switchIfEmpty(publisherRepositoryOutPort.save(publisher))
    }


    override fun getAllPublishers(): Flux<Publisher> {
        return publisherRepositoryOutPort.findAll()
    }

    override fun deletePublisher(publisherName: String): Mono<Unit> {
        return publisherRepositoryOutPort.deleteByPublisherName(publisherName)
            .handle { deletedCount, sync ->
                if (deletedCount == 0L) {
                    sync.error(NotFoundException("Publisher with publisherName - $publisherName not found"))
                }
            }
    }

    override fun postContent(publisherName: String, content: String): Mono<Post> {
        return publisherRepositoryOutPort.findByPublisherName(publisherName)
            .flatMap { publisher ->
                postRepositoryOutPort.save(Post(creatorId = publisher.id!!, content = content))
            }
            .switchIfEmpty { NotFoundException("Internal error, failed to save new post").toMono() }
    }

    override fun getPostsByPublisherName(publisherName: String): Flux<Post> {
        return publisherRepositoryOutPort.findByPublisherName(publisherName)
            .switchIfEmpty { NotFoundException("Publisher not found with given publisherName = $publisherName")
                .toMono()
            }
            .flatMapMany { publisher -> findAllPostsByCreatorIdOrderByCreatedAt(publisher) }
    }

    private fun findAllPostsByCreatorIdOrderByCreatedAt(mongoPublisher: Publisher): Flux<Post> {
        val publisherId = mongoPublisher.id!!
        return postRepositoryOutPort.findAllPostsByCreatorIdOrderByCreatedAt(publisherId)
            .switchIfEmpty (NotFoundException("Publisher not found with given id = ${publisherId.toHexString()}")
                .toMono())
    }
}
