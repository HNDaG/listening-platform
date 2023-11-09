package com.nikitahohulia.listeningplatform.user.application.svc


import com.nikitahohulia.listeningplatform.core.application.exception.DuplicateException
import com.nikitahohulia.listeningplatform.core.application.exception.NotFoundException
import com.nikitahohulia.listeningplatform.post.application.port.PostRepositoryOutPort
import com.nikitahohulia.listeningplatform.post.domain.Post
import com.nikitahohulia.listeningplatform.publisher.application.port.PublisherRepositoryOutPort
import com.nikitahohulia.listeningplatform.publisher.domain.Publisher
import com.nikitahohulia.listeningplatform.user.application.port.UserRepositoryOutPort
import com.nikitahohulia.listeningplatform.user.application.port.UserServiceInPort
import com.nikitahohulia.listeningplatform.user.application.port.UserEventProducerOutPort
import com.nikitahohulia.listeningplatform.user.domain.User
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import reactor.kotlin.core.publisher.toMono
import reactor.kotlin.core.util.function.component1
import reactor.kotlin.core.util.function.component2

@Service
class UserService(
    @Qualifier("cacheableUserRepository") private val userRepository: UserRepositoryOutPort,
    private val publisherRepositoryOutPort: PublisherRepositoryOutPort,
    private val postRepositoryOutPort: PostRepositoryOutPort,
    private val userProducer: UserEventProducerOutPort
) : UserServiceInPort {

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
        return publisherRepositoryOutPort.findByPublisherName(publisher.publisherName)
            .handle<Publisher> { _, sync ->
                sync.error(DuplicateException("Publisher with the same PublisherName already exists"))
            }
            .switchIfEmpty {
                userRepository.findByUsername(username)
                    .filter { user -> user.publisherId == null }
                    .switchIfEmpty { DuplicateException("User is already a publisher").toMono() }
                    .zipWhen { publisherRepositoryOutPort.save(publisher) }
                    .flatMap { (user, newPublisher) ->
                        val userAsPublisher = user.copy(publisherId = newPublisher.id)
                        userRepository.save(userAsPublisher)
                            .doOnNext { userProducer.publishEvent(it) }
                            .thenReturn(newPublisher)
                    }
            }
    }

    override fun deleteUsersPublisher(username: String, publisherName: String): Mono<Unit> {
        return publisherRepositoryOutPort.deleteByPublisherName(publisherName)
            .switchIfEmpty { NotFoundException("There is no publisher with publisherName = $publisherName").toMono() }
            .flatMap { userRepository.findByUsername(username) }
            .switchIfEmpty { NotFoundException("There is no user to delete publisher from").toMono() }
            .flatMap { user ->
                userRepository.save(user.copy(publisherId = null))
                    .doOnNext { userProducer.publishEvent(it) }
            }
            .then(Mono.empty())
    }

    override fun updateUser(oldUsername: String, user: User): Mono<User> {
        return userRepository.findByUsername(oldUsername)
            .switchIfEmpty { NotFoundException("User not found with username = $oldUsername").toMono() }
            .flatMap { userRepository.save(user.copy(id = it.id)) }
            .doOnNext { userProducer.publishEvent(it) }
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
            .zipWhen { publisherRepositoryOutPort.findByPublisherName(publisherName) }
            .switchIfEmpty { NotFoundException("Publisher not found with publisherName = $publisherName").toMono() }
            .flatMap { (user, publisher) ->
                user.subscriptions.add(publisher.id!!)
                userRepository.save(user)
            }
            .doOnNext { userProducer.publishEvent(it) }
            .thenReturn(Unit)
    }

    override fun getPostsFromFollowedCreators(username: String, page: Int): Flux<Post> {
        val ids = userRepository.findPublisherIdsByUsername(username)
        return postRepositoryOutPort.findAllBySubscriptionIds(ids, page, DEFAULT_PAGE_SIZE)
    }

    private companion object {
        const val DEFAULT_PAGE_SIZE: Int = 5
    }
}
