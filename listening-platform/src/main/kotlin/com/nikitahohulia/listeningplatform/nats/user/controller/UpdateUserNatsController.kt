package com.nikitahohulia.listeningplatform.nats.user.controller

import com.google.protobuf.Parser
import com.nikitahohulia.listeningplatform.nats.NatsController
import com.nikitahohulia.listeningplatform.dto.request.toEntity
import com.nikitahohulia.listeningplatform.dto.response.toProto
import com.nikitahohulia.listeningplatform.exception.NotFoundException
import com.nikitahohulia.listeningplatform.service.UserService
import com.nikitahohulia.api.internal.v2.usersvc.NatsSubject
import com.nikitahohulia.api.internal.v2.usersvc.commonmodels.user.User
import com.nikitahohulia.api.internal.v2.usersvc.input.reqreply.update.proto.UpdateUserRequest
import com.nikitahohulia.api.internal.v2.usersvc.input.reqreply.update.proto.UpdateUserResponse
import io.nats.client.Connection
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Component
class UpdateUserNatsController(
    override val connection: Connection,
    private val userService: UserService
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
