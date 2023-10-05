package com.nikitahohulia.listeningplatform.controller.nats.user

import com.google.protobuf.Parser
import com.nikitahohulia.listeningplatform.controller.nats.NatsController
import com.nikitahohulia.listeningplatform.dto.response.toProto
import com.nikitahohulia.listeningplatform.service.UserService
import com.nikitahohulia.nats.NatsSubject
import com.nikitahohulia.nats.commonmodels.user.User
import com.nikitahohulia.nats.reqreply.user.get_by_id.proto.GetUserByIdRequestCommon
import com.nikitahohulia.nats.reqreply.user.get_by_id.proto.GetUserByIdResponseCommon
import io.nats.client.Connection
import org.springframework.stereotype.Component

@Component
class GetUserByIdNatsController(
    override val connection: Connection,
    private val userService: UserService
) : NatsController<GetUserByIdRequestCommon, GetUserByIdResponseCommon> {

    override val subject = NatsSubject.User.GET_BY_ID
    override val parser: Parser<GetUserByIdRequestCommon> = GetUserByIdRequestCommon.parser()

    override fun handle(request: GetUserByIdRequestCommon): GetUserByIdResponseCommon = runCatching {

        buildSuccessResponse(userService.getUserById(request.userId).toProto())

    }.getOrElse { exception ->
        buildFailureResponse(exception.javaClass.simpleName, exception.toString())
    }

    private fun buildSuccessResponse(user: User): GetUserByIdResponseCommon =
        GetUserByIdResponseCommon.newBuilder().apply {
            successBuilder
                .setUser(user)
        }.build()

    private fun buildFailureResponse(exception: String, message: String): GetUserByIdResponseCommon =
        GetUserByIdResponseCommon.newBuilder().apply {
            failureBuilder
                .setMessage("GetUserById failed by $exception: $message")
        }.build()
}
