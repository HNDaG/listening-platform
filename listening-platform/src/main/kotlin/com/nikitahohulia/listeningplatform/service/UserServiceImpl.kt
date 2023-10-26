package com.nikitahohulia.listeningplatform.service

import com.nikitahohulia.listeningplatform.entity.Post
import com.nikitahohulia.listeningplatform.entity.Publisher
import com.nikitahohulia.listeningplatform.entity.User
import com.nikitahohulia.listeningplatform.exception.DuplicateException
import com.nikitahohulia.listeningplatform.exception.NotFoundException
import com.nikitahohulia.listeningplatform.repository.PostRepository
import com.nikitahohulia.listeningplatform.repository.PublisherRepository
import com.nikitahohulia.listeningplatform.repository.UserRepository
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
    private val postRepository: PostRepository
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
        username: String, publisher: Publisher
    ): Mono<Publisher> {
        return publisherRepository.findByPublisherName(publisher.publisherName)
            .handle<Publisher> { _, sync ->
                sync.error(DuplicateException("Publisher with the same PublisherName already exists"))
            }
            .switchIfEmpty {
                userRepository.findByUsername(username)
                    .filter { user -> user.publisherId == null }
                    .switchIfEmpty { DuplicateException("User is already a publisher").toMono() }
                    .zipWhen { publisherRepository.save(publisher) }.flatMap { (user, newPublisher) ->
                        val userAsPublisher = user.copy(publisherId = newPublisher.id)
                        userRepository.save(userAsPublisher)
                            .thenReturn(newPublisher)
                    }
            }
    }


    override fun updateUser(oldUsername: String, user: User): Mono<User> {
        return userRepository.findByUsername(oldUsername)
            .switchIfEmpty { NotFoundException("User not found with username = $oldUsername").toMono() }
            .flatMap {
                userRepository.save(user.copy(id = it.id))
            }
    }

    override fun getAllUsers(): Flux<User> {
        return userRepository.findAll()
    }

    override fun deleteUserByUsername(username: String): Mono<Unit> {
        return userRepository.deleteUserByUsername(username).handle { deletedCount, sync ->
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
            }.thenReturn(Unit)
    }

    override fun getPostsFromFollowedCreators(username: String, page: Int): Flux<Post> {
        val ids = userRepository.findPublisherIdsByUsername(username)
        return postRepository.findAllBySubscriptionIds(ids, page, DEFAULT_PAGE_SIZE)
    }

    private companion object {
        const val DEFAULT_PAGE_SIZE: Int = 5
    }
}
