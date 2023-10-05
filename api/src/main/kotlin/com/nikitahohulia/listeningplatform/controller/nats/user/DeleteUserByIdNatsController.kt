package com.nikitahohulia.listeningplatform.controller.nats.user

import com.google.protobuf.Parser
import com.nikitahohulia.listeningplatform.controller.nats.NatsController
import com.nikitahohulia.listeningplatform.service.UserService
import com.nikitahohulia.nats.NatsSubject
import com.nikitahohulia.nats.reqreply.user.get_by_id.proto.DeleteUserByIdRequestCommon
import com.nikitahohulia.nats.reqreply.user.get_by_id.proto.DeleteUserByIdResponseCommon
import io.nats.client.Connection
import org.springframework.stereotype.Component

@Component
class DeleteUserByIdNatsController(
    override val connection: Connection,
    private val userService: UserService
) : NatsController<DeleteUserByIdRequestCommon, DeleteUserByIdResponseCommon> {

    override val subject = NatsSubject.User.DELETE_BY_ID
    override val parser: Parser<DeleteUserByIdRequestCommon> = DeleteUserByIdRequestCommon.parser()

    override fun handle(request: DeleteUserByIdRequestCommon): DeleteUserByIdResponseCommon = runCatching {
        userService.deleteUserById(request.userId)

        buildSuccessResponse()

    }.getOrElse { exception ->
        buildFailureResponse(exception.javaClass.simpleName, exception.toString())
    }

    private fun buildSuccessResponse(): DeleteUserByIdResponseCommon =
        DeleteUserByIdResponseCommon.newBuilder().apply {
            successBuilder.build()
        }.build()

    private fun buildFailureResponse(exception: String, message: String): DeleteUserByIdResponseCommon =
        DeleteUserByIdResponseCommon.newBuilder().apply {
            failureBuilder
                .setMessage("User deleteById failed by $exception: $message")
        }.build()
}
