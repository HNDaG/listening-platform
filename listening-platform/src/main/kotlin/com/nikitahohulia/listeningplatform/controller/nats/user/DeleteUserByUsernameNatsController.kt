package com.nikitahohulia.listeningplatform.controller.nats.user

import com.google.protobuf.Parser
import com.nikitahohulia.listeningplatform.controller.nats.NatsController
import com.nikitahohulia.listeningplatform.service.UserService
import com.nikitahohulia.nats.NatsSubject
import com.nikitahohulia.nats.reqreply.user.get_by_id.proto.DeleteUserByUsernameRequest
import com.nikitahohulia.nats.reqreply.user.get_by_id.proto.DeleteUserByUsernameResponse
import io.nats.client.Connection
import org.springframework.stereotype.Component

@Component
class DeleteUserByUsernameNatsController(
    override val connection: Connection,
    private val userService: UserService
) : NatsController<DeleteUserByUsernameRequest, DeleteUserByUsernameResponse> {

    override val subject = NatsSubject.User.DELETE_BY_USERNAME
    override val parser: Parser<DeleteUserByUsernameRequest> = DeleteUserByUsernameRequest.parser()

    override fun handle(request: DeleteUserByUsernameRequest): DeleteUserByUsernameResponse = runCatching {
        userService.deleteUserByUsername(request.username)

        buildSuccessResponse()
    }.getOrElse { exception ->
        buildFailureResponse(exception.javaClass.simpleName, exception.toString())
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

