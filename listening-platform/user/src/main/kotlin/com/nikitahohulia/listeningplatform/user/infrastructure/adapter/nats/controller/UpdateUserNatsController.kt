package com.nikitahohulia.listeningplatform.user.infrastructure.adapter.nats.controller

import com.google.protobuf.Parser
import com.nikitahohulia.api.internal.v2.usersvc.NatsSubject
import com.nikitahohulia.api.internal.v2.usersvc.commonmodels.user.User
import com.nikitahohulia.api.internal.v2.usersvc.input.reqreply.update.proto.UpdateUserRequest
import com.nikitahohulia.api.internal.v2.usersvc.input.reqreply.update.proto.UpdateUserResponse
import com.nikitahohulia.listeningplatform.core.application.exception.NotFoundException
import com.nikitahohulia.listeningplatform.core.infrastructure.adapter.nats.NatsController
import com.nikitahohulia.listeningplatform.user.application.port.UserServiceInPort
import com.nikitahohulia.listeningplatform.user.infrastructure.mapper.toEntity
import com.nikitahohulia.listeningplatform.user.infrastructure.mapper.toProto
import io.nats.client.Connection
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Component
class UpdateUserNatsController(
    override val connection: Connection,
    private val userService: UserServiceInPort
): NatsController<UpdateUserRequest, UpdateUserResponse> {

    override val subject = NatsSubject.User.UPDATE
    override val parser: Parser<UpdateUserRequest> = UpdateUserRequest.parser()

    override fun handleHelper(request: UpdateUserRequest): Mono<UpdateUserResponse> {
        if (request.user.id == null)
            throw NotFoundException("There is no id passed")

        return userService.updateUser(request.oldUsername, request.user.toEntity())
            .map { buildSuccessResponse(it.toProto()) }
            .onErrorResume { ex ->
                buildFailureResponse(
                    ex.javaClass.simpleName,
                    ex.toString()
                ).toMono()
            }
    }

    private fun buildSuccessResponse(user: User): UpdateUserResponse {
        return UpdateUserResponse.newBuilder().apply {
            successBuilder.user = user
        }.build()
    }

    private fun buildFailureResponse(exception: String, message: String): UpdateUserResponse =
        UpdateUserResponse.newBuilder().apply {
            failureBuilder.message = "CreateUser failed by $exception: $message"
        }.build()
}
