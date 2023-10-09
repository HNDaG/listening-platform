package com.nikitahohulia.listeningplatform.controller.nats.user

import com.google.protobuf.Parser
import com.nikitahohulia.listeningplatform.controller.nats.NatsController
import com.nikitahohulia.listeningplatform.dto.request.toEntity
import com.nikitahohulia.listeningplatform.dto.response.toProto
import com.nikitahohulia.listeningplatform.exception.EntityNotFoundException
import com.nikitahohulia.listeningplatform.service.UserService
import com.nikitahohulia.api.internal.v2.usersvc.NatsSubject
import com.nikitahohulia.api.internal.v2.usersvc.commonmodels.user.User
import com.nikitahohulia.api.internal.v2.usersvc.input.reqreply.create.proto.CreateUserRequest
import com.nikitahohulia.api.internal.v2.usersvc.input.reqreply.create.proto.CreateUserResponse
import io.nats.client.Connection
import org.springframework.stereotype.Component

@Component
class UpdateUserNatsController(
    override val connection: Connection,
    private val userService: UserService
): NatsController<CreateUserRequest, CreateUserResponse> {

    override val subject = NatsSubject.User.UPDATE
    override val parser: Parser<CreateUserRequest> = CreateUserRequest.parser()

    override fun handle(request: CreateUserRequest): CreateUserResponse = runCatching {
        if (request.user.id == null)
            throw EntityNotFoundException("There is no id passed")
        val savedUser = userService.updateUser(request.user.id, request.user.toEntity())

        buildSuccessResponse(savedUser.toProto())
    }.getOrElse { exception ->
        buildFailureResponse(exception.javaClass.simpleName, exception.toString())
    }

    private fun buildSuccessResponse(user: User): CreateUserResponse {
        return CreateUserResponse.newBuilder().apply {
            successBuilder
                .setUser(user)
        }.build()
    }

    private fun buildFailureResponse(exception: String, message: String): CreateUserResponse =
        CreateUserResponse.newBuilder().apply {
            failureBuilder
                .setMessage("CreateUser failed by $exception: $message")
        }.build()
}
