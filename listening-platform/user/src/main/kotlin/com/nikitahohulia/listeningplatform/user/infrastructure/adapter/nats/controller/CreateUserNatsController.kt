package com.nikitahohulia.listeningplatform.user.infrastructure.adapter.nats.controller

import com.google.protobuf.Parser
import com.nikitahohulia.api.internal.v2.usersvc.NatsSubject.User.CREATE
import io.nats.client.Connection
import org.springframework.stereotype.Component
import com.nikitahohulia.api.internal.v2.usersvc.commonmodels.user.User
import com.nikitahohulia.api.internal.v2.usersvc.input.reqreply.create.proto.CreateUserRequest
import com.nikitahohulia.api.internal.v2.usersvc.input.reqreply.create.proto.CreateUserResponse
import com.nikitahohulia.listeningplatform.core.infrastructure.adapter.nats.NatsController
import com.nikitahohulia.listeningplatform.user.application.port.UserService
import com.nikitahohulia.listeningplatform.user.infrastructure.mapper.toEntity
import com.nikitahohulia.listeningplatform.user.infrastructure.mapper.toProto
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Component
class CreateUserNatsController(
    override val connection: Connection,
    private val userService: UserService
) : NatsController<CreateUserRequest, CreateUserResponse> {

    override val subject = CREATE
    override val parser: Parser<CreateUserRequest> = CreateUserRequest.parser()

    override fun handleHelper(request: CreateUserRequest): Mono<CreateUserResponse> {
        val user = request.user.toEntity()

        return userService.createUser(user)
            .map { buildSuccessResponse(it.toProto()) }
            .onErrorResume { ex ->
                buildFailureResponse(
                    ex.javaClass.simpleName,
                    ex.toString()
                ).toMono()
            }
    }

    private fun buildSuccessResponse(user: User): CreateUserResponse =
        CreateUserResponse.newBuilder().apply {
            successBuilder.user = user
        }.build()

    private fun buildFailureResponse(exception: String, message: String): CreateUserResponse =
        CreateUserResponse.newBuilder().apply {
            failureBuilder.message = "CreateUser failed by $exception: $message"
        }.build()
}
