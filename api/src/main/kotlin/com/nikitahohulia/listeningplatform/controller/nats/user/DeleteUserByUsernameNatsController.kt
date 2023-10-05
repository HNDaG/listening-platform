package com.nikitahohulia.listeningplatform.controller.nats.user

import com.google.protobuf.Parser
import com.nikitahohulia.listeningplatform.controller.nats.NatsController
import com.nikitahohulia.listeningplatform.service.UserService
import com.nikitahohulia.nats.NatsSubject
import com.nikitahohulia.nats.reqreply.user.get_by_id.proto.DeleteUserByUsernameRequestCommon
import com.nikitahohulia.nats.reqreply.user.get_by_id.proto.DeleteUserByUsernameResponseCommon
import io.nats.client.Connection
import org.springframework.stereotype.Component

@Component
class DeleteUserByUsernameNatsController(
    override val connection: Connection,
    private val userService: UserService
) : NatsController<DeleteUserByUsernameRequestCommon, DeleteUserByUsernameResponseCommon> {
    override val subject = NatsSubject.User.DELETE_BY_USERNAME
    override val parser: Parser<DeleteUserByUsernameRequestCommon> = DeleteUserByUsernameRequestCommon.parser()

    override fun handle(request: DeleteUserByUsernameRequestCommon): DeleteUserByUsernameResponseCommon = runCatching {
        userService.deleteUserByUsername(request.username)

        buildSuccessResponse()

    }.getOrElse { exception ->
        buildFailureResponse(exception.javaClass.simpleName, exception.toString())
    }

    private fun buildSuccessResponse(): DeleteUserByUsernameResponseCommon =
        DeleteUserByUsernameResponseCommon.newBuilder().apply {
            successBuilder.build()
        }.build()

    private fun buildFailureResponse(exception: String, message: String): DeleteUserByUsernameResponseCommon =
        DeleteUserByUsernameResponseCommon.newBuilder().apply {
            failureBuilder
                .setMessage("User deleteByUsername failed by $exception: $message")
        }.build()
}

