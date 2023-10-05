package com.nikitahohulia.listeningplatform.controller.nats.user

import com.google.protobuf.Parser
import com.nikitahohulia.listeningplatform.controller.nats.NatsController
import com.nikitahohulia.listeningplatform.dto.response.toProto
import com.nikitahohulia.listeningplatform.service.UserService
import com.nikitahohulia.nats.NatsSubject
import com.nikitahohulia.nats.commonmodels.user.User
import com.nikitahohulia.nats.reqreply.user.get_all.proto.GetAllUsersRequestCommon
import com.nikitahohulia.nats.reqreply.user.get_all.proto.GetAllUsersResponseCommon
import io.nats.client.Connection
import org.springframework.stereotype.Component

@Component
class GetAllUsersNatsController(
    override val connection: Connection,
    private val userService: UserService
): NatsController<GetAllUsersRequestCommon, GetAllUsersResponseCommon> {

    override val subject = NatsSubject.User.GET_ALL
    override val parser: Parser<GetAllUsersRequestCommon> = GetAllUsersRequestCommon.parser()

    override fun handle(request: GetAllUsersRequestCommon): GetAllUsersResponseCommon = runCatching {

        buildSuccessResponse(userService.getAllUsers().map { it.toProto() })

    }.getOrElse { exception ->
        buildFailureResponse(exception.javaClass.simpleName, exception.toString())
    }

    private fun buildSuccessResponse(userList: List<User>): GetAllUsersResponseCommon =
        GetAllUsersResponseCommon.newBuilder().apply {
            successBuilder
                .usersBuilder
                .addAllUsers(userList)
        }.build()

    private fun buildFailureResponse(exception: String, message: String): GetAllUsersResponseCommon =
        GetAllUsersResponseCommon.newBuilder().apply {
            failureBuilder
                .setMessage("GetAllUsers failed by $exception: $message")
        }.build()
}
