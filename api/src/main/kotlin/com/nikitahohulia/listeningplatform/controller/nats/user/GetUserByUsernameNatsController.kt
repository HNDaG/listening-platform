package com.nikitahohulia.listeningplatform.controller.nats.user

import com.google.protobuf.Parser
import com.nikitahohulia.listeningplatform.controller.nats.NatsController
import com.nikitahohulia.listeningplatform.dto.response.toProto
import com.nikitahohulia.listeningplatform.service.UserService
import com.nikitahohulia.nats.NatsSubject
import com.nikitahohulia.nats.commonmodels.user.User
import com.nikitahohulia.nats.reqreply.user.get_by_id.proto.GetUserByUsernameRequestCommon
import com.nikitahohulia.nats.reqreply.user.get_by_id.proto.GetUserByUsernameResponseCommon
import io.nats.client.Connection
import org.springframework.stereotype.Component

@Component
class GetUserByUsernameNatsController(
    override val connection: Connection,
    private val userService: UserService
) : NatsController<GetUserByUsernameRequestCommon, GetUserByUsernameResponseCommon> {
    override val subject = NatsSubject.User.GET_BY_USERNAME
    override val parser: Parser<GetUserByUsernameRequestCommon> = GetUserByUsernameRequestCommon.parser()

    override fun handle(request: GetUserByUsernameRequestCommon): GetUserByUsernameResponseCommon = runCatching {
        val username: String = request.username
        val user = userService.getUserByUsername(username)

        buildSuccessResponse(user.toProto())

    }.getOrElse { exception ->
        buildFailureResponse(exception.javaClass.simpleName, exception.toString())
    }

    private fun buildSuccessResponse(user: User): GetUserByUsernameResponseCommon =
        GetUserByUsernameResponseCommon.newBuilder().apply {
            successBuilder
                .setUser(user)
        }.build()

    private fun buildFailureResponse(exception: String, message: String): GetUserByUsernameResponseCommon =
        GetUserByUsernameResponseCommon.newBuilder().apply {
            failureBuilder
                .setMessage("GetUserByUsername failed by $exception: $message")
        }.build()
}
