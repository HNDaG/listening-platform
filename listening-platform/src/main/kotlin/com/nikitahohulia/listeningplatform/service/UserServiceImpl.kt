package com.nikitahohulia.listeningplatform.service

import com.nikitahohulia.listeningplatform.entity.Post
import com.nikitahohulia.listeningplatform.entity.Publisher
import com.nikitahohulia.listeningplatform.entity.User
import com.nikitahohulia.listeningplatform.exception.DuplicateException
import com.nikitahohulia.listeningplatform.exception.NotFoundException
import com.nikitahohulia.listeningplatform.redis.repositiry.UserRedisRepository
import com.nikitahohulia.listeningplatform.repository.CustomPostRepository
import com.nikitahohulia.listeningplatform.repository.CustomUserRepository
import com.nikitahohulia.listeningplatform.repository.CustomPublisherRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import reactor.kotlin.core.publisher.toMono
import reactor.kotlin.core.util.function.component1
import reactor.kotlin.core.util.function.component2

@Service
class UserServiceImpl(
    private val userRepository: CustomUserRepository,
    private val publisherRepository: CustomPublisherRepository,
    private val postRepository: CustomPostRepository,
    private val redisUserRepository: UserRedisRepository
) : UserService {

    override fun getUserByUsername(username: String): Mono<User> {
        return redisUserRepository.findByUsername(username)
            .switchIfEmpty {
                userRepository.findByUsername(username)
                    .flatMap { user ->
                        redisUserRepository.save(user).thenReturn(user)
                    }
                    .switchIfEmpty(NotFoundException(message = "User not found with username = $username").toMono())
            }
    }

    override fun createUser(user: User): Mono<User> {
        return redisUserRepository.findByUsername(user.username)
            .switchIfEmpty {
                userRepository.findByUsername(user.username)
            }
            .handle<User> { _, sync ->
                sync.error(DuplicateException("User with this username already exists"))
            }
            .switchIfEmpty(userRepository.save(user)
                .flatMap { savedUser ->
                redisUserRepository.save(savedUser).thenReturn(savedUser)
            })
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
                redisUserRepository.findByUsername(username)
                    .switchIfEmpty {
                        userRepository.findByUsername(username)
                    }
                    .filter { user -> user.publisherId == null }
                    .switchIfEmpty { DuplicateException("User is already a publisher").toMono() }
                    .zipWhen { publisherRepository.save(publisher) }
                    .flatMap { (user, newPublisher) ->
                        val userAsPublisher = user.copy(publisherId = newPublisher.id)
                        userRepository.save(userAsPublisher).then(redisUserRepository.update(userAsPublisher))
                            .thenReturn(newPublisher)
                    }
            }
    }


    override fun updateUser(oldUsername: String, user: User): Mono<User> {
        return userRepository.findByUsername(oldUsername)
            .switchIfEmpty { NotFoundException("User not found with username = $oldUsername").toMono() }
            .flatMap {
                userRepository.save(user.copy(id = it.id))
                    .flatMap { savedUser ->
                        redisUserRepository.update(savedUser).thenReturn(savedUser)
                    }
            }
    }

    override fun getAllUsers(): Flux<User> {
        return userRepository.findAll()
    }

    override fun deleteUserByUsername(username: String): Mono<Unit> {
        return userRepository.deleteUserByUsername(username)
            .handle<Unit?> { deletedCount, sync ->
                if (deletedCount == 0L) {
                    sync.error(NotFoundException("User with username - $username not found"))
                }
            }
            .then(redisUserRepository.deleteByUsername(username))
    }

    override fun subscribe(username: String, publisherName: String): Mono<Unit> {
        return userRepository.findByUsername(username)
            .zipWhen { publisherRepository.findByPublisherName(publisherName) }
            .switchIfEmpty { NotFoundException("Publisher not found with publisherName = $publisherName").toMono() }
            .flatMap { (user, publisher) ->
                user.subscriptions.add(publisher.id!!)
                userRepository.save(user)
                redisUserRepository.update(user)
            }
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
