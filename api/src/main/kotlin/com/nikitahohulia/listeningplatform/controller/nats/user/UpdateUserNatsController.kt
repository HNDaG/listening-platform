package com.nikitahohulia.listeningplatform.controller.nats.user

import com.google.protobuf.Parser
import com.nikitahohulia.listeningplatform.controller.nats.NatsController
import com.nikitahohulia.listeningplatform.dto.request.toEntity
import com.nikitahohulia.listeningplatform.dto.request.toRequest
import com.nikitahohulia.listeningplatform.dto.response.toProto
import com.nikitahohulia.listeningplatform.service.UserService
import com.nikitahohulia.nats.NatsSubject
import com.nikitahohulia.nats.commonmodels.user.User
import com.nikitahohulia.nats.reqreply.user.create.proto.CreateUserRequestCommon
import com.nikitahohulia.nats.reqreply.user.create.proto.CreateUserResponseCommon
import io.nats.client.Connection
import org.springframework.stereotype.Component

@Component
class UpdateUserNatsController(
    override val connection: Connection,
    private val userService: UserService
): NatsController<CreateUserRequestCommon, CreateUserResponseCommon> {
    override val subject = NatsSubject.User.UPDATE
    override val parser: Parser<CreateUserRequestCommon> = CreateUserRequestCommon.parser()

    override fun handle(request: CreateUserRequestCommon): CreateUserResponseCommon = runCatching {
        val user = request.user
        if (user.id == null)
            throw RuntimeException("There is no id passed")
        val savedUser = userService.updateUser(user.id, user.toRequest().toEntity())

        buildSuccessResponse(savedUser.toProto())

    }.getOrElse { exception ->
        buildFailureResponse(exception.javaClass.simpleName, exception.toString())
    }

    private fun buildSuccessResponse(user: User): CreateUserResponseCommon =
        CreateUserResponseCommon.newBuilder().apply {
            successBuilder
                .setUser(user)
        }.build()

    private fun buildFailureResponse(exception: String, message: String): CreateUserResponseCommon =
        CreateUserResponseCommon.newBuilder().apply {
            failureBuilder
                .setMessage("CreateUser failed by $exception: $message")
        }.build()
}
