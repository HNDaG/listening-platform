package com.nikitahohulia.listeningplatform.user.infrastructure.adapter.nats.controller

import com.google.protobuf.Parser
import com.nikitahohulia.api.internal.v2.usersvc.NatsSubject
import com.nikitahohulia.api.internal.v2.usersvc.input.reqreply.delete_by_username.proto.DeleteUserByUsernameRequest
import com.nikitahohulia.api.internal.v2.usersvc.input.reqreply.delete_by_username.proto.DeleteUserByUsernameResponse
import com.nikitahohulia.listeningplatform.core.infrastructure.adapter.nats.NatsController
import com.nikitahohulia.listeningplatform.user.application.port.UserServiceInPort
import io.nats.client.Connection
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Component
class DeleteUserByUsernameNatsController(
    override val connection: Connection,
    private val userService: UserServiceInPort
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
            failureBuilder.message = "User deleteByUsername failed by $exception: $message"
        }.build()
}
