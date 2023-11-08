package com.nikitahohulia.listeningplatform.user.application.service


import com.nikitahohulia.listeningplatform.core.application.exception.DuplicateException
import com.nikitahohulia.listeningplatform.core.application.exception.NotFoundException
import com.nikitahohulia.listeningplatform.post.application.port.PostRepository
import com.nikitahohulia.listeningplatform.post.domain.Post
import com.nikitahohulia.listeningplatform.publisher.application.port.PublisherRepository
import com.nikitahohulia.listeningplatform.publisher.domain.Publisher
import com.nikitahohulia.listeningplatform.user.application.port.UserRepository
import com.nikitahohulia.listeningplatform.user.application.port.UserService
import com.nikitahohulia.listeningplatform.user.domain.User
import com.nikitahohulia.listeningplatform.user.infrastructure.adapter.kafka.producer.UserKafkaProducer
import com.nikitahohulia.listeningplatform.user.infrastructure.mapper.toProto
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import reactor.kotlin.core.publisher.toMono
import reactor.kotlin.core.util.function.component1
import reactor.kotlin.core.util.function.component2

@Service
class UserServiceImpl(
    @Qualifier("cacheableUserRepository") private val userRepository: UserRepository,
    private val publisherRepository: PublisherRepository,
    private val postRepository: PostRepository,
    private val kafkaUserProducer: UserKafkaProducer
) : UserService {

    override fun getUserByUsername(username: String): Mono<User> {
        return userRepository.findByUsername(username)
            .switchIfEmpty(NotFoundException(message = "User not found with username = $username").toMono())
    }

    override fun createUser(user: User): Mono<User> {
        return userRepository.findByUsername(user.username)
            .handle<User> { _, sync ->
                sync.error(DuplicateException("User with this username already exists"))
            }
            .switchIfEmpty {
                userRepository.save(user)
            }
    }

    override fun becamePublisher(
        username: String, 
        publisher: Publisher
    ): Mono<Publisher> {
        return publisherRepository.findByPublisherName(publisher.publisherName)
            .handle<Publisher> { _, sync ->
                sync.error(DuplicateException("Publisher with the same PublisherName already exists"))
            }
            .switchIfEmpty {
                userRepository.findByUsername(username)
                    .filter { user -> user.publisherId == null }
                    .switchIfEmpty { DuplicateException("User is already a publisher").toMono() }
                    .zipWhen { publisherRepository.save(publisher) }
                    .flatMap { (user, newPublisher) ->
                        val userAsPublisher = user.copy(publisherId = newPublisher.id)
                        userRepository.save(userAsPublisher)
                            .doOnNext { kafkaUserProducer.sendUserUpdatedEventToKafka(it.toProto()) }
                            .thenReturn(newPublisher)
                    }
            }
    }

    override fun deleteUsersPublisher(username: String, publisherName: String): Mono<Unit> {
        return publisherRepository.deleteByPublisherName(publisherName)
            .switchIfEmpty { NotFoundException("There is no publisher with publisherName = $publisherName").toMono() }
            .flatMap { userRepository.findByUsername(username) }
            .switchIfEmpty { NotFoundException("There is no user to delete publisher from").toMono() }
            .flatMap { user ->
                userRepository.save(user.copy(publisherId = null))
                    .doOnNext { kafkaUserProducer.sendUserUpdatedEventToKafka(it.toProto()) }
            }
            .then(Mono.empty())
    }

    override fun updateUser(oldUsername: String, user: User): Mono<User> {
        return userRepository.findByUsername(oldUsername)
            .switchIfEmpty { NotFoundException("User not found with username = $oldUsername").toMono() }
            .flatMap { userRepository.save(user.copy(id = it.id)) }
            .doOnNext { kafkaUserProducer.sendUserUpdatedEventToKafka(it.toProto()) }
            .flatMap { userRepository.findByUsername(it.username) }
    }

    override fun getAllUsers(): Flux<User> {
        return userRepository.findAll()
    }

    override fun deleteUserByUsername(username: String): Mono<Unit> {
        return userRepository.deleteUserByUsername(username)
            .handle { deletedCount, sync ->
                if (deletedCount == 0L) {
                    sync.error(NotFoundException("User with username - $username not found"))
                }
            }
    }

    override fun subscribe(username: String, publisherName: String): Mono<Unit> {
        return userRepository.findByUsername(username)
            .zipWhen { publisherRepository.findByPublisherName(publisherName) }
            .switchIfEmpty { NotFoundException("Publisher not found with publisherName = $publisherName").toMono() }
            .flatMap { (user, publisher) ->
                user.subscriptions.add(publisher.id!!)
                userRepository.save(user)
            }
            .doOnNext { kafkaUserProducer.sendUserUpdatedEventToKafka(it.toProto()) }
            .thenReturn(Unit)
    }

    override fun getPostsFromFollowedCreators(username: String, page: Int): Flux<Post> {
        val ids = userRepository.findPublisherIdsByUsername(username)
        return postRepository.findAllBySubscriptionIds(ids, page, DEFAULT_PAGE_SIZE)
    }

    private companion object {
        const val DEFAULT_PAGE_SIZE: Int = 5
    }
}