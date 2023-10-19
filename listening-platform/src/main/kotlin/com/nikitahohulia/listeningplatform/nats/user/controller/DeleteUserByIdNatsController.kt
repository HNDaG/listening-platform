package com.nikitahohulia.listeningplatform.nats.user.controller

import com.google.protobuf.Parser
import com.nikitahohulia.listeningplatform.service.UserService
import com.nikitahohulia.api.internal.v2.usersvc.NatsSubject
import com.nikitahohulia.api.internal.v2.usersvc.input.reqreply.user.get_by_id.proto.DeleteUserByIdRequest
import com.nikitahohulia.api.internal.v2.usersvc.input.reqreply.user.get_by_id.proto.DeleteUserByIdResponse
import com.nikitahohulia.listeningplatform.nats.NatsController
import io.nats.client.Connection
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Component
class DeleteUserByIdNatsController(
    override val connection: Connection,
    private val userService: UserService
) : NatsController<DeleteUserByIdRequest, DeleteUserByIdResponse> {

    override val subject = NatsSubject.User.DELETE_BY_ID
    override val parser: Parser<DeleteUserByIdRequest> = DeleteUserByIdRequest.parser()

    override fun handleHelper(request: DeleteUserByIdRequest): Mono<DeleteUserByIdResponse> {
        val id = request.userId

        return userService.deleteUserById(id)
            .then(buildSuccessResponse().toMono())
            .onErrorResume { ex ->
                buildFailureResponse(
                    ex.javaClass.simpleName,
                    ex.toString()
                ).toMono() }
    }

    private fun buildSuccessResponse(): DeleteUserByIdResponse =
        DeleteUserByIdResponse.newBuilder().apply {
            successBuilder.build()
        }.build()

    private fun buildFailureResponse(exception: String, message: String): DeleteUserByIdResponse =
        DeleteUserByIdResponse.newBuilder().apply {
            failureBuilder
                .setMessage("User deleteById failed by $exception: $message")
        }.build()
}
