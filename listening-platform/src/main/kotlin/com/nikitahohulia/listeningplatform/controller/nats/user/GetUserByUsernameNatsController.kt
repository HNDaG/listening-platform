package com.nikitahohulia.listeningplatform.controller.nats.user

import com.google.protobuf.Parser
import com.nikitahohulia.listeningplatform.controller.nats.NatsController
import com.nikitahohulia.listeningplatform.dto.response.toProto
import com.nikitahohulia.listeningplatform.service.UserService
import com.nikitahohulia.nats.NatsSubject
import com.nikitahohulia.nats.commonmodels.user.User
import com.nikitahohulia.nats.reqreply.user.get_by_id.proto.GetUserByUsernameRequest
import com.nikitahohulia.nats.reqreply.user.get_by_id.proto.GetUserByUsernameResponse
import io.nats.client.Connection
import org.springframework.stereotype.Component

@Component
class GetUserByUsernameNatsController(
    override val connection: Connection,
    private val userService: UserService
) : NatsController<GetUserByUsernameRequest, GetUserByUsernameResponse> {

    override val subject = NatsSubject.User.GET_BY_USERNAME
    override val parser: Parser<GetUserByUsernameRequest> = GetUserByUsernameRequest.parser()

    override fun handle(request: GetUserByUsernameRequest): GetUserByUsernameResponse = runCatching {
        val user = userService.getUserByUsername(request.username)

        buildSuccessResponse(user.toProto())
    }.getOrElse { exception ->
        buildFailureResponse(exception.javaClass.simpleName, exception.toString())
    }

    private fun buildSuccessResponse(user: User): GetUserByUsernameResponse =
        GetUserByUsernameResponse.newBuilder().apply {
            successBuilder
                .setUser(user)
        }.build()

    private fun buildFailureResponse(exception: String, message: String): GetUserByUsernameResponse =
        GetUserByUsernameResponse.newBuilder().apply {
            failureBuilder
                .setMessage("GetUserByUsername failed by $exception: $message")
        }.build()
}
