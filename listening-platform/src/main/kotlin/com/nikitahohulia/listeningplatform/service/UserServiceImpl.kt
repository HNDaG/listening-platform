package com.nikitahohulia.listeningplatform.service

import com.mongodb.MongoException
import com.nikitahohulia.listeningplatform.dto.request.PublisherDtoRequest
import com.nikitahohulia.listeningplatform.dto.request.UserDtoRequest
import com.nikitahohulia.listeningplatform.dto.request.toEntity
import com.nikitahohulia.listeningplatform.dto.response.PostDtoResponse
import com.nikitahohulia.listeningplatform.dto.response.PublisherDtoResponse
import com.nikitahohulia.listeningplatform.dto.response.UserDtoResponse
import com.nikitahohulia.listeningplatform.dto.response.toResponse
import com.nikitahohulia.listeningplatform.entity.User
import com.nikitahohulia.listeningplatform.exception.DuplicateException
import com.nikitahohulia.listeningplatform.exception.EntityNotFoundException
import com.nikitahohulia.listeningplatform.exception.NotFoundException
import com.nikitahohulia.listeningplatform.repository.CustomPostRepository
import com.nikitahohulia.listeningplatform.repository.CustomUserRepository
import com.nikitahohulia.listeningplatform.repository.CustomPublisherRepository
import org.bson.types.ObjectId
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import reactor.kotlin.core.publisher.toMono

@Service
class UserServiceImpl(
    private val userRepository: CustomUserRepository,
    private val publisherRepository: CustomPublisherRepository,
    private val postRepository: CustomPostRepository
) : UserService {

    override fun getUserByUsername(username: String): Mono<UserDtoResponse> {
        return userRepository.findByUsername(username).map { it.toResponse() }
            .switchIfEmpty {
                NotFoundException("User not found with given username = $username")
                    .toMono()
            }
    }

    override fun getUserById(id: String): Mono<UserDtoResponse> {
        return userRepository.findById(ObjectId(id)).map { it.toResponse() }
            .switchIfEmpty {
                NotFoundException("User not found with given id = $id")
                    .toMono()
            }
    }

    override fun createUser(userDtoRequest: UserDtoRequest): Mono<UserDtoResponse> {
        return userRepository.findByUsername(userDtoRequest.username)
            .handle<UserDtoResponse> { _, sync ->
                sync.error(DuplicateException("User with this username already exists"))
            }
            .switchIfEmpty(
                userRepository.save(userDtoRequest.toEntity()).map { it.toResponse() }
            )
    }

    override fun becamePublisher(
        username: String,
        publisherDtoRequest: PublisherDtoRequest
    ): Mono<PublisherDtoResponse> {
        return publisherRepository.findByPublisherName(publisherDtoRequest.publisherName)
            .handle<PublisherDtoResponse> { _, sync ->
                sync.error(DuplicateException("Publisher with the same PublisherName already exists"))
            }
            .switchIfEmpty {
                userRepository.findByUsername(username)
                    .flatMap { user ->
                        if (user.publisherId != null) {
                            DuplicateException("User is already a publisher").toMono()
                        } else {
                            publisherRepository.save(publisherDtoRequest.toEntity())
                                .flatMap { newPublisher ->
                                    userRepository.save(user.copy(publisherId = newPublisher.id))
                                        .flatMap {
                                            newPublisher.toResponse().toMono()
                                        }
                                }
                        }
                    }
                    .switchIfEmpty {
                        IllegalArgumentException("Database internal fail").toMono()
                    }
            }
    }


    override fun updateUser(id: String, user: User): Mono<UserDtoResponse> {
        return userRepository.findById(ObjectId(id))
            .switchIfEmpty {
                NotFoundException("User not found with given id = $id")
                    .toMono()
            }
            .flatMap {
                val updatedUser = user.copy(id = ObjectId(id))
                userRepository.save(updatedUser).map { it.toResponse() }
            }
    }

    override fun getAllUsers(): Flux<UserDtoResponse> {
        return userRepository.findAll().map { it.toResponse() }
    }

    override fun deleteUserById(id: String): Mono<Unit> {
        return userRepository.deleteById(ObjectId(id))
            .handle { deletedCount, sync ->
                if (deletedCount == 0L) {
                    sync.error(EntityNotFoundException("User with ID - $id not found"))
                }
            }
    }

    override fun deleteUserByUsername(username: String): Mono<Unit> {
        return userRepository.deleteUserByUsername(username)
            .handle { deletedCount, sync ->
                if (deletedCount == 0L) {
                    sync.error(EntityNotFoundException("User with username - $username not found"))
                }
            }
    }

    override fun subscribe(username: String, publisherName: String): Mono<Unit> {
        val userMono = userRepository.findByUsername(username)
            .switchIfEmpty { NotFoundException("User not found with given username = $username").toMono() }

        return userMono
            .flatMap { user ->
                publisherRepository.findByPublisherName(publisherName)
                    .switchIfEmpty {
                        NotFoundException("Publisher not found with given publisherName = $publisherName")
                            .toMono()
                    }
                    .switchIfEmpty { Mono.error(MongoException("Failed to subscribe")) }
                    .flatMap { publisher ->
                        user.subscriptions.add(publisher.id!!)
                        userRepository.save(user)
                        Mono.empty()
                    }
            }
    }

    override fun getPostsFromFollowedCreators(username: String, page: Int): Flux<PostDtoResponse> {
        val ids = userRepository.findPublisherIdsByUsername(username)
        return postRepository.findAllBySubscriptionIds(ids, page, DEFAULT_PAGE_SIZE)
            .map { it.toResponse() }
    }

    private companion object {
        const val DEFAULT_PAGE_SIZE: Int = 5
    }
}
