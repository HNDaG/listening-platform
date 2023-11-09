package com.nikitahohulia.listeningplatform.user.infrastructure.adapter.nats.controller

import com.google.protobuf.Parser
import com.nikitahohulia.api.internal.v2.usersvc.NatsSubject
import com.nikitahohulia.api.internal.v2.usersvc.commonmodels.user.User
import com.nikitahohulia.api.internal.v2.usersvc.input.reqreply.get_all.proto.GetAllUsersRequest
import com.nikitahohulia.api.internal.v2.usersvc.input.reqreply.get_all.proto.GetAllUsersResponse
import com.nikitahohulia.listeningplatform.core.infrastructure.adapter.nats.NatsController
import com.nikitahohulia.listeningplatform.user.application.port.UserServiceInPort
import com.nikitahohulia.listeningplatform.user.infrastructure.mapper.toProto
import io.nats.client.Connection
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Component
class GetAllUsersNatsController(
    override val connection: Connection,
    private val userService: UserServiceInPort
): NatsController<GetAllUsersRequest, GetAllUsersResponse> {

    override val subject = NatsSubject.User.GET_ALL
    override val parser: Parser<GetAllUsersRequest> = GetAllUsersRequest.parser()

    override fun handleHelper(request: GetAllUsersRequest): Mono<GetAllUsersResponse> {
        return userService.getAllUsers()
            .collectList()
            .map { buildSuccessResponse(it.map { user -> user.toProto() }) }
            .onErrorResume { ex ->
                buildFailureResponse(
                    ex.javaClass.simpleName,
                    ex.toString()
                ).toMono()
            }
    }

    private fun buildSuccessResponse(userList: List<User>): GetAllUsersResponse =
        GetAllUsersResponse.newBuilder().apply {
            successBuilder
                .usersBuilder
                .addAllUsers(userList)
        }.build()

    private fun buildFailureResponse(exception: String, message: String): GetAllUsersResponse =
        GetAllUsersResponse.newBuilder().apply {
            failureBuilder.message = "GetAllUsers failed by $exception: $message"
        }.build()
}
