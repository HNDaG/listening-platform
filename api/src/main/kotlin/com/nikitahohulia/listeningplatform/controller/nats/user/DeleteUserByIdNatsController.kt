package com.nikitahohulia.listeningplatform.controller.nats.user

import com.google.protobuf.Parser
import com.nikitahohulia.listeningplatform.controller.nats.NatsController
import com.nikitahohulia.listeningplatform.service.UserService
import com.nikitahohulia.nats.NatsSubject
import com.nikitahohulia.nats.reqreply.user.get_by_id.proto.DeleteUserByIdRequest
import com.nikitahohulia.nats.reqreply.user.get_by_id.proto.DeleteUserByIdResponse
import io.nats.client.Connection
import org.springframework.stereotype.Component

@Component
class DeleteUserByIdNatsController(
    override val connection: Connection,
    private val userService: UserService
) : NatsController<DeleteUserByIdRequest, DeleteUserByIdResponse> {

    override val subject = NatsSubject.User.DELETE_BY_ID
    override val parser: Parser<DeleteUserByIdRequest> = DeleteUserByIdRequest.parser()

    override fun handle(request: DeleteUserByIdRequest): DeleteUserByIdResponse = runCatching {
        userService.deleteUserById(request.userId)

        buildSuccessResponse()

    }.getOrElse { exception ->
        buildFailureResponse(exception.javaClass.simpleName, exception.toString())
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
