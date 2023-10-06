package com.nikitahohulia.listeningplatform.controller.nats.user

import com.google.protobuf.Parser
import com.nikitahohulia.listeningplatform.controller.nats.NatsController
import com.nikitahohulia.listeningplatform.dto.request.toRequest
import com.nikitahohulia.listeningplatform.dto.response.toProto
import com.nikitahohulia.listeningplatform.service.UserService
import com.nikitahohulia.nats.NatsSubject.User.CREATE
import com.nikitahohulia.nats.reqreply.user.create.proto.CreateUserRequest
import com.nikitahohulia.nats.reqreply.user.create.proto.CreateUserResponse
import io.nats.client.Connection
import org.springframework.stereotype.Component
import com.nikitahohulia.nats.commonmodels.user.User


@Component
class CreateUserNatsController(
    override val connection: Connection,
    private val userService: UserService
): NatsController<CreateUserRequest, CreateUserResponse> {

    override val subject = CREATE
    override val parser: Parser<CreateUserRequest> = CreateUserRequest.parser()

    override fun handle(request: CreateUserRequest): CreateUserResponse = runCatching {
        val savedUser = userService.createUser(request.user.toRequest())

        buildSuccessResponse(savedUser.toProto())
    }.getOrElse { exception ->
        buildFailureResponse(exception.javaClass.simpleName, exception.toString())
    }

    private fun buildSuccessResponse(user: User): CreateUserResponse =
        CreateUserResponse.newBuilder().apply {
            successBuilder
                .setUser(user)
        }.build()

    private fun buildFailureResponse(exception: String, message: String): CreateUserResponse =
        CreateUserResponse.newBuilder().apply {
            failureBuilder
                .setMessage("CreateUser failed by $exception: $message")
        }.build()
}
