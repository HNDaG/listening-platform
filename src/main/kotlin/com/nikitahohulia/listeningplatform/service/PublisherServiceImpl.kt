package com.nikitahohulia.listeningplatform.service

import com.nikitahohulia.listeningplatform.entity.Post
import com.nikitahohulia.listeningplatform.entity.Publisher
import com.nikitahohulia.listeningplatform.repository.PostRepository
import com.nikitahohulia.listeningplatform.repository.PublisherRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class PublisherServiceImpl(
    private val publisherRepository: PublisherRepository,
    private val postRepository: PostRepository
) : PublisherService {
    override fun getPublisherById(id: Long): Publisher? {
        return publisherRepository.findByIdOrNull(id)
    }

    override fun getPublisherByPublisherName(publisherName: String): Publisher? {
        return publisherRepository.findByPublisherName(publisherName)
    }

    override fun createPublisher(publisher: Publisher): Publisher {
        return publisherRepository.save(publisher)
    }

    override fun getAllPublishers(): List<Publisher> {
        return publisherRepository.findAll()
    }

    override fun deletePublisher(id: Long) {
        publisherRepository.deleteById(id)
    }

    override fun postContent(post: Post): Post {
        return postRepository.save(post)
    }
}
