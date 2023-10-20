package com.nikitahohulia.listeningplatform.nats.user.controller

import com.google.protobuf.Parser
import com.nikitahohulia.listeningplatform.service.UserService
import com.nikitahohulia.api.internal.v2.usersvc.NatsSubject
import com.nikitahohulia.api.internal.v2.usersvc.input.reqreply.get_by_id.proto.DeleteUserByUsernameRequest
import com.nikitahohulia.api.internal.v2.usersvc.input.reqreply.get_by_id.proto.DeleteUserByUsernameResponse
import com.nikitahohulia.listeningplatform.nats.NatsController
import io.nats.client.Connection
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Component
class DeleteUserByUsernameNatsController(
    override val connection: Connection,
    private val userService: UserService
) : NatsController<DeleteUserByUsernameRequest, DeleteUserByUsernameResponse> {

    override val subject = NatsSubject.User.DELETE_BY_USERNAME
    override val parser: Parser<DeleteUserByUsernameRequest> = DeleteUserByUsernameRequest.parser()

    override fun handleHelper(request: DeleteUserByUsernameRequest): Mono<DeleteUserByUsernameResponse> {
        val username = request.username

        return userService.deleteUserByUsername(username)
            .then(buildSuccessResponse().toMono())
            .onErrorResume { ex ->
                buildFailureResponse(
                    ex.javaClass.simpleName,
                    ex.toString()
                ).toMono()
            }
    }

    private fun buildSuccessResponse(): DeleteUserByUsernameResponse =
        DeleteUserByUsernameResponse.newBuilder().apply {
            successBuilder.build()
        }.build()

    private fun buildFailureResponse(exception: String, message: String): DeleteUserByUsernameResponse =
        DeleteUserByUsernameResponse.newBuilder().apply {
            failureBuilder
                .setMessage("User deleteByUsername failed by $exception: $message")
        }.build()
}

